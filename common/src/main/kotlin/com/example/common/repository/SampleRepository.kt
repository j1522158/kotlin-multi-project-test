package com.example.common.repository

import com.example.common.entity.SampleEntity
import org.apache.ibatis.annotations.Delete

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select


@Mapper
interface SampleRepository {
    @Select("select * from sample")
    fun findAll(): List<SampleEntity?>?

    @Insert("insert into sample (text, description) values (#{text}, #{description})")
    fun insert(text: String, description: String)

    @Select("select * from sample where id = #{id}")
    fun findById(id: Long): SampleEntity?

    @Delete("delete from sample where id = #{id}")
    fun deleteById(id: Long)
}