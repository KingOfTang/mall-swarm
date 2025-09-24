package com.macro.mall.mapper;

import com.macro.mall.model.DevTestLog;
import com.macro.mall.model.DevTestLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DevTestLogMapper {
    long countByExample(DevTestLogExample example);

    int deleteByExample(DevTestLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DevTestLog row);

    int insertSelective(DevTestLog row);

    List<DevTestLog> selectByExample(DevTestLogExample example);

    DevTestLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DevTestLog row, @Param("example") DevTestLogExample example);

    int updateByExample(@Param("row") DevTestLog row, @Param("example") DevTestLogExample example);

    int updateByPrimaryKeySelective(DevTestLog row);

    int updateByPrimaryKey(DevTestLog row);
}