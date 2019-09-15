package com.aether.customerservice.dao;

import com.aether.customerapi.entity.EmergencyContact;
import org.apache.ibatis.annotations.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Mapper
public interface EmergencyContactDao {


    @InsertProvider(type = Provider.class, method = "batchInsert")
    int insertCollectList(List<EmergencyContact> contactList);

    @Update(" UPDATE t_emergency_contact SET relation = #{contact.relation},   name = #{contact.name},   mobile = #{contact.mobile},  country_code = #{contact.countryCode},   update_date = now()  WHERE   id = #{contact.id} ")
    void update(@Param("contact") EmergencyContact contact);

    @Select("select id,user_id userId,relation,name,mobile from t_emergency_contact where user_id=#{userId} order by create_date")
    List<EmergencyContact> queryEmergencyContacts(@Param("userId") String userId);

    public class Provider {
        /* 批量插入 */
        public String batchInsert(Map map) {
            List<EmergencyContact> students = (List<EmergencyContact>) map.get("list");
            StringBuilder sb = new StringBuilder();
            sb.append("insert into t_emergency_contact(id, user_id, relation,name,mobile,country_code,create_date) values ");
            MessageFormat mf = new MessageFormat(
                    "(#'{'list[{0}].id}, #'{'list[{0}].userId}, #'{'list[{0}].relation}, #'{'list[{0}].name}, #'{'list[{0}].mobile}, #'{'list[{0}].countryCode}, #'{'list[{0}].createDate})"
            );

            for (int i = 0; i < students.size(); i++) {
                sb.append(mf.format(new Object[] {i}));
                if (i < students.size() - 1) {
                    sb.append(",");
                }
            }
            return sb.toString();
        }
    }
}
