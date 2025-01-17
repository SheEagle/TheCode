package com.example.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.*;
import com.example.entity.vo.request.AddCommentVO;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.request.TopicUpdateVO;
import com.example.entity.vo.response.CommentVO;
import com.example.entity.vo.response.TopicDetailsVO;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.entity.vo.response.TopicTopVO;
import com.example.mapper.*;
import com.example.service.NotificationService;
import com.example.service.TopicService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Resource
    TopicTypeMapper mapper;

    @Resource
    FlowUtils flowUtils;

    @Resource
    CacheUtils cacheUtils;

    @Resource
    AccountMapper accountMapper;

    @Resource
    AccountDetailsMapper accountDetailsMapper;
    @Resource
    AccountPrivacyMapper accountPrivacyMapper;
    @Resource
    StringRedisTemplate template;

    @Resource
    TopicCommentMapper commentMapper;

    @Resource
    HotnessMapper hotnessMapper;

    @Resource
    NotificationService notificationService;

    private Set<Integer> types = null;

    @PostConstruct
    private void initTypes() {
        types = this.listTypes()
                .stream()
                .map(TopicType::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public List<TopicType> listTypes() {
        return mapper.selectList(null);
    }

    @Override
    public String createTopic(int uid, TopicCreateVO vo) {

        if (!textLimitCheck(vo.getContent(), 20000))
            return "文章内容太长，发帖失败";
        if (!types.contains(vo.getType()))
            return "文章类型非法";

        String content = String.valueOf(vo.getContent());

        try {
            // 构建请求 URL
            String apiUrl = "https://api.pearktrue.cn/api/sensitivewords/?text=" + URLEncoder.encode(content, StandardCharsets.UTF_8);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 发送请求并获取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 判断连接是否成功
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 解析响应
                JSONObject jsonResponse = JSON.parseObject(response.toString());
                int code = jsonResponse.getIntValue("code");
                String msg = jsonResponse.getString("msg");

                // 根据返回的 code 判断是否有敏感词
                if (code == 200) {
                    // 检测到敏感词
                    JSONObject data = jsonResponse.getJSONObject("data");
                    String filteredText = data.getString("text");
                    JSONArray detectedWords = data.getJSONArray("detected_words");

                    // 检查并过滤敏感词
                    for (int i = 0; i < detectedWords.size(); i++) {
                        String word = detectedWords.getString(i);
                        // 检查是否包含中文
                        if (word.matches(".*[\u4e00-\u9fa5]+.*")) {
                            // 替换为星号，例如："电话" 替换为 "****"
                            content = content.replaceAll(Pattern.quote(word), "\\*".repeat(word.length()));
                        }
                    }
                    // 返回过滤后的内容
                    Topic topic = new Topic();
                    BeanUtils.copyProperties(vo, topic);
                    topic.setContent(content);
                    topic.setUid(uid);
                    topic.setTime(new Date());
                    if (this.save(topic)) {
                        cacheUtils.deleteCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
                        return null;
                    } else {
                        return "内部错误，请联系管理员！";
                    }
                } else if (code == 201) {
                    // 没有敏感词，继续发帖
                    Topic topic = new Topic();
                    BeanUtils.copyProperties(vo, topic);
                    topic.setContent(vo.getContent().toJSONString());
                    topic.setUid(uid);
                    topic.setTime(new Date());
                    if (this.save(topic)) {
                        cacheUtils.deleteCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
                        return null;
                    } else {
                        return "内部错误，请联系管理员！";
                    }
                } else {
                    // 其他错误码处理
                    return "敏感词检测失败，请稍后再试";
                }
            } else {
                // HTTP 请求非200响应的情况
                return "敏感词检测服务暂不可用，请稍后再试";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "敏感词检测过程中发生错误，请稍后再试";
        }

    }


    @Override
    public List<TopicPreviewVO> listTopicByPage(int pageNumber, int type, String search) {

        Page<Hotness> page = new Page<>(pageNumber, 10);
        QueryWrapper<Hotness> queryWrapper = new QueryWrapper<>();

        // 按热度查询
        if (type == -1) {

            if (search != null && !search.isEmpty()) {
                queryWrapper
                        .and(w -> w.like("title", search).or().like("content", search))
                        .orderByDesc("hotness");
            } else {
                queryWrapper.orderByDesc("hotness");
            }
        }
        // 按类型和关键词查询
        else {
            // 当type不为0时，查询特定类型的帖子
            if (type != 0) {
                queryWrapper.eq("type", type);
            }
            // 无论type为何值，都可以添加搜索关键词的条件
            if (search != null && !search.isEmpty()) {
                queryWrapper
                        .and(w -> w.like("title", search).or().like("content", search));
            }
            // 如果不需要按时间排序，可以移除下面这行
            queryWrapper.orderByDesc("time");
        }

        IPage<Hotness> hotnessPage = hotnessMapper.selectPage(page, queryWrapper);
        List<Hotness> hotnessList = hotnessPage.getRecords();
        if (hotnessList.isEmpty())
            return null;

        List<TopicPreviewVO> list = hotnessList.stream().map(this::resolveToPreview).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<TopicTopVO> listTopTopics() {
        List<Topic> topics = baseMapper.selectList(Wrappers.<Topic>query()
                .select("id", "title", "time")
                .eq("top", 1));
        return topics.stream().map(topic -> {
            TopicTopVO vo = new TopicTopVO();
            BeanUtils.copyProperties(topic, vo);
            return vo;
        }).toList();
    }

    @Override
    public TopicDetailsVO getTopic(int tid, int uid) {
        TopicDetailsVO vo = new TopicDetailsVO();
        Topic topic = baseMapper.selectById(tid);
        BeanUtils.copyProperties(topic, vo);
        TopicDetailsVO.Interact interact = new TopicDetailsVO.Interact(
                hasInteract(tid, uid, "like"),
                hasInteract(tid, uid, "collect")
        );
        vo.setInteract(interact);
        TopicDetailsVO.User user = new TopicDetailsVO.User();
        vo.setUser(this.fillUserDetailsByPrivacy(user, topic.getUid()));
        vo.setComments(commentMapper.selectCount(Wrappers.<TopicComment>query().eq("tid", tid)));
        return vo;
    }

    @Override
    public void deleteTopic(int tid, int uid) {
        // 使用LambdaQueryWrapper构建查询条件
        LambdaQueryWrapper<Topic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Topic::getId, tid).eq(Topic::getUid, uid);

        // 使用remove方法删除记录
        this.remove(queryWrapper);

    }

    @Override
    public void deleteTopicByAdmin(int tid, String rule) {
        // 根据帖子ID查找帖子
        LambdaQueryWrapper<Topic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Topic::getId, tid);
        Topic topic = this.getOne(queryWrapper);

        if (topic != null) {
            String title = topic.getTitle();
            int uid = topic.getUid();

            // 删除帖子
            this.remove(queryWrapper);

            String message = "管理员删除了您的帖子：'" + title + "'";
            notificationService.addNotification(
                    uid,
                    "您的帖子疑似" + rule + ",已被管理员删除",
                    message,
                    "danger",
                    "/index"
            );

        }
    }

    @Override
    public void interact(Interact interact, boolean state) {
        String type = interact.getType();
        synchronized (type.intern()) {
            template.opsForHash().put(type, interact.toKey(), Boolean.toString(state));
            this.saveInteractSchedule(type);

        }
    }

    @Override
    public List<TopicPreviewVO> listTopicCollects(int uid) {
        return baseMapper.collectTopics(uid)
                .stream()
                .map(topic -> {
                    TopicPreviewVO vo = new TopicPreviewVO();
                    BeanUtils.copyProperties(topic, vo);
                    return vo;
                })
                .toList();
    }

    @Override
    public String updateTopic(int uid, TopicUpdateVO vo) {
        if (!textLimitCheck(vo.getContent(), 20000))
            return "文章内容太长，发帖失败";
        if (!types.contains(vo.getType()))
            return "文章类型非法";
        baseMapper.update(null, Wrappers.<Topic>update()
                .eq("uid", uid)
                .eq("id", vo.getId())
                .set("title", vo.getTitle())
                .set("content", vo.getContent().toString())
                .set("type", vo.getType())
        );
        return null;
    }

    @Override
    public String createComment(int uid, AddCommentVO vo) {
        if (!textLimitCheck(JSONObject.parseObject(vo.getContent()), 2000))
            return "评论内容太长，发表失败";
        String key = Const.FORUM_TOPIC_COMMENT_COUNTER + uid;
        String commentContent = vo.getContent();
        if (!flowUtils.limitPeriodCounterCheck(key, 2, 60))
            return "发表评论频繁，请稍后再试";
        try {
            // 构建请求 URL
            String apiUrl = "https://api.pearktrue.cn/api/sensitivewords/?text=" + URLEncoder.encode(commentContent, StandardCharsets.UTF_8);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 发送请求并获取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 判断连接是否成功
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 解析响应
                JSONObject jsonResponse = JSON.parseObject(response.toString());
                int code = jsonResponse.getIntValue("code");
                String msg = jsonResponse.getString("msg");

                // 根据返回的 code 判断是否有敏感词
                if (code == 200) {
                    // 检测到敏感词
                    JSONObject data = jsonResponse.getJSONObject("data");
                    String filteredText = data.getString("text");
                    JSONArray detectedWords = data.getJSONArray("detected_words");

                    // 检查并过滤敏感词
                    for (int i = 0; i < detectedWords.size(); i++) {
                        String word = detectedWords.getString(i);
                        // 检查是否包含中文
                        if (word.matches(".*[\u4e00-\u9fa5]+.*")) {
                            // 替换为星号，例如："电话" 替换为 "****"
                            commentContent = commentContent.replaceAll(Pattern.quote(word), "\\*".repeat(word.length()));
                        }
                    }
                    TopicComment comment = new TopicComment();
                    comment.setUid(uid);
                    vo.setContent(commentContent);
                    BeanUtils.copyProperties(vo, comment);
                    comment.setTime(new Date());
                    commentMapper.insert(comment);
                    return null;

                } else if (code == 201) {
                    // 没有敏感词，继续创建评论
                    TopicComment comment = new TopicComment();
                    comment.setUid(uid);
                    BeanUtils.copyProperties(vo, comment);
                    comment.setTime(new Date());
                    commentMapper.insert(comment);
                    return null;
                } else {
                    // 其他错误码处理
                    return "敏感词检测失败，请稍后再试";
                }
            } else {
                // HTTP 请求非200响应的情况
                return "敏感词检测服务暂不可用，请稍后再试";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "敏感词检测过程中发生错误，请稍后再试";
        }

    }

    @Override
    public List<CommentVO> comments(int tid, int pageNumber) {
        // 创建分页对象
        Page<TopicComment> page = Page.of(pageNumber, 10);
        // 查询指定帖子的评论并填充到分页对象中
        commentMapper.selectPage(page, Wrappers.<TopicComment>query().eq("tid", tid));
        // 将查询结果转换为CommentVO列表并返回
        return page.getRecords().stream().map(dto -> {
            CommentVO vo = new CommentVO();
            // 复制属性值
            BeanUtils.copyProperties(dto, vo);
            // 检查引用评论是否存在且有效
            if (dto.getQuote() != null && dto.getQuote() > 0) {
                // 查询引用评论
                Optional<TopicComment> topicCommentOptional = Optional.ofNullable(commentMapper.selectOne(Wrappers.<TopicComment>query().eq("id", dto.getQuote()).orderByAsc("time")));
                // 如果引用评论存在
                if (topicCommentOptional.isPresent()) {
                    TopicComment topicComment = topicCommentOptional.get();
                    JSONObject object = JSONObject.parseObject(topicComment.getContent());
                    // 获取引用评论内容
                    StringBuilder builder = new StringBuilder();
                    this.shortContent(object.getJSONArray("ops"), builder, ignore -> {
                    });
                    // 设置引用评论内容到CommentVO对象中
                    vo.setQuote(builder.toString());
                } else {
                    // 如果引用评论不存在，设置一条提示信息到CommentVO对象中
                    vo.setQuote("此评论已被删除");
                }
            }
            // 填充用户详情到CommentVO对象中
            CommentVO.User user = new CommentVO.User();
            this.fillUserDetailsByPrivacy(user, dto.getUid());
            vo.setUser(user);
            return vo;
        }).toList();
    }


    @Override
    public void deleteComment(int id, int uid) {
        commentMapper.delete(Wrappers.<TopicComment>query().eq("id", id).eq("uid", uid));
    }

    @Override
    public void deleteCommentByAdmin(int id, String rule) {
        // 首先，使用id查询评论内容
        TopicComment comment = commentMapper.selectById(id);
        if (comment == null) {
            // 如果没有找到评论，可能已经被删除或者id无效
            return;
        }

        String content = "";
        int uid = comment.getUid();

        try {
            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();
            // 将字符串解析为 JsonNode
            JsonNode rootNode = objectMapper.readTree(comment.getContent());

            // 遍历 ops 数组
            StringBuilder textContent = new StringBuilder();
            JsonNode opsNode = rootNode.path("ops");
            if (opsNode.isArray()) {
                for (JsonNode opNode : opsNode) {
                    // 获取 insert 字段的值
                    String insertValue = opNode.path("insert").asText();
                    // 将值添加到 StringBuilder
                    textContent.append(insertValue);
                }
            }
            content = textContent.toString().trim();
            // 打印提取的文本内容
            System.out.println(content);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 然后，使用Wrappers构建查询条件来删除评论
        QueryWrapper<TopicComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);

        // 执行删除操作
        int isDeleted = commentMapper.delete(queryWrapper);
        if (isDeleted > 0) {
            // 如果删除成功，添加通知
            String message = "管理员删除了您的评论：'" + content + "'";
            notificationService.addNotification(
                    uid,
                    "您的评论疑似" + rule + ",已被管理员移除",
                    message,
                    "danger",
                    "/index"
            );
        }

    }

    private boolean hasInteract(int tid, int uid, String type) {
        String key = tid + ":" + uid;
        if (template.opsForHash().hasKey(type, key)) {
            return Boolean.parseBoolean(template.opsForHash().entries(type).get(key).toString());
        }
        return baseMapper.userInteractCount(tid, uid, type) > 0;
    }

    private final Map<String, Boolean> state = new HashMap<>();

    ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

    private void saveInteractSchedule(String type) {
        if (!state.getOrDefault(type, false)) {
            state.put(type, true);
            service.schedule(() -> {
                this.saveInteract(type);
                state.put(type, false);
            }, 3, TimeUnit.SECONDS);
        }
    }

    private void saveInteract(String type) {
        synchronized (type.intern()) {
            List<Interact> check = new LinkedList<>();
            List<Interact> uncheck = new LinkedList<>();
            template.opsForHash().entries(type).forEach((k, v) -> {
                if (Boolean.parseBoolean(v.toString())) {
                    check.add(Interact.parseInteract(k.toString(), type));
                } else {
                    uncheck.add(Interact.parseInteract(k.toString(), type));
                }
            });
            if (!check.isEmpty())
                baseMapper.addInteract(check, type);
            if (!uncheck.isEmpty())
                baseMapper.deleteInteract(uncheck, type);
            template.delete(type);
        }
    }

    private <T> T fillUserDetailsByPrivacy(T target, int uid) {
        AccountDetails details = accountDetailsMapper.selectById(uid);
        Account account = accountMapper.selectById(uid);
        AccountPrivacy privacy = accountPrivacyMapper.selectById(uid);
        String[] ignores = privacy.hiddenFields();
        BeanUtils.copyProperties(account, target, ignores);
        BeanUtils.copyProperties(details, target, ignores);
        return target;
    }


    private TopicPreviewVO resolveToPreview(Hotness topic) {
        TopicPreviewVO vo = new TopicPreviewVO();
        BeanUtils.copyProperties(accountMapper.selectById(topic.getUid()), vo);
        BeanUtils.copyProperties(topic, vo);
        vo.setLike(baseMapper.interactCount(topic.getTopicId(), "like"));
        vo.setCollect(baseMapper.interactCount(topic.getTopicId(), "collect"));
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        this.shortContent(ops, previewText, obj -> images.add(obj.toString()));
        vo.setText(previewText.length() > 300 ? previewText.substring(0, 300) : previewText.toString());
        vo.setImages(images);
        vo.setId(topic.getTopicId());
        return vo;
    }

    private void shortContent(JSONArray ops, StringBuilder previewText, Consumer<Object> imageHandler) {
        for (Object op : ops) {
            Object insert = JSONObject.from(op).get("insert");
            if (insert instanceof String text) {
                if (previewText.length() >= 300)
                    continue;
                previewText.append(text);
            } else if (insert instanceof Map<?, ?> map) {
                Optional.ofNullable(map.get("image"))
                        .ifPresent(imageHandler);
            }
        }
    }

    private boolean textLimitCheck(JSONObject object, int max) {
        if (object == null)
            return false;
        long length = 0;
        for (Object op : object.getJSONArray("ops")) {
            length += JSONObject.from(op).getString("insert").length();
            if (length > max)
                return false;

        }
        return true;
    }
}
