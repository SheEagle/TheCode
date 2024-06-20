package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Interact;
import com.example.entity.dto.Topic;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

import org.apache.ibatis.annotations.Param;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {

//    @Select("""
//                select * from db_topic left join db_account on uid=db_account.id
//                order by `time` desc limit ${start},10
//            """)
//    List<Topic> topicList(int start);
//
//
//    @Select("""
//                select * from db_topic left join db_account on uid=db_account.id
//                where type=#{type}
//                order by `time` desc limit ${start},10
//            """)
//    List<Topic> topicList2(int start,int type);

    @Insert("""
            <script>
                insert ignore into db_topic_interact_${type} values
                <foreach collection ="interacts" item="item" separator =",">
                    (#{item.tid}, #{item.uid}, #{item.time})
                </foreach>
            </script>           
            """)
    void addInteract(List<Interact> interacts, String type);

    @Delete("""
            <script>
                delete from db_topic_interact_${type} where
                <foreach collection="interacts" item="item" separator=" or ">
                    (tid = #{item.tid} and uid = #{item.uid})
                </foreach>
            </script>
            """)
    int deleteInteract(List<Interact> interacts, String type);


    @Select("""
            select count(*) from db_topic_interact_${type} where tid = #{tid}
            """)
    int interactCount(int tid, String type);

    @Select("""
            select count(*) from db_topic_interact_${type} where tid = #{tid} and uid = #{uid}
            """)
    int userInteractCount(int tid, int uid, String type);

    @Select("""
            select * from db_topic_interact_collect left join db_topic on tid = db_topic.id
             where db_topic_interact_collect.uid = #{uid}
            """)
    List<Topic> collectTopics(int uid);

    //    @Select("""
//            select t.*,
//                   (select count(*) from db_topic_interact_like where tid = t.id) as likeCount,
//                   (select count(*) from db_topic_interact_collect where tid = t.id) as collectCount,
//                   (select count(*) from db_topic_comment where tid = t.id) as commentCount
//            from db_topic t
//            order by (likeCount * 2 + collectCount * 1.5 + commentCount * 3) desc
//            limit #{pageNumber}, 10
//            """)
//    List<Topic> listHotTopics(int pageNumber);
    @Select("""
                select t.*, 
                       (select count(*) from db_topic_interact_like where tid = t.id) as likeCount,
                       (select count(*) from db_topic_interact_collect where tid = t.id) as collectCount,
                       (select count(*) from db_topic_comment where tid = t.id) as commentCount
                from db_topic t
                order by (likeCount * 2 + collectCount * 1.5 + commentCount * 3) desc
                limit #{pageNumber}, 10
            """)
    List<Topic> listHotTopics(@Param("pageNumber") int pageNumber);

    @Select("""
                select t.*, 
                       (select count(*) from db_topic_interact_like where tid = t.id) as likeCount,
                       (select count(*) from db_topic_interact_collect where tid = t.id) as collectCount,
                       (select count(*) from db_topic_comment where tid = t.id) as commentCount
                from db_topic t
                where t.title like concat('%', #{search}, '%') or t.content like concat('%', #{search}, '%')
                order by (likeCount * 2 + collectCount * 1.5 + commentCount * 3) desc
                limit #{start}, 10
            """)
    List<Topic> listHotTopicsWithSearch(@Param("search") String search, @Param("start") int start);


}
