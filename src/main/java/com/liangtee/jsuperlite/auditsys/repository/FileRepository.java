package com.liangtee.jsuperlite.core.repository;

import com.liangtee.jsuperlite.core.model.FileInfo;
import com.liangtee.jsuperlite.core.repository.base.BaseRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/5/29.
 * All rights Reserved
 */

public interface FileRepository extends CrudRepository<FileInfo, String> {


}
