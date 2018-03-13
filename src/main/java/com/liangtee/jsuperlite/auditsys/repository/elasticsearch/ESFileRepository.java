package com.liangtee.jsuperlite.core.repository.elasticsearch;

import com.liangtee.jsuperlite.core.model.FileInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by Allen on 2017/7/15.
 */
public interface ESFileRepository extends ElasticsearchRepository<FileInfo, String> {
}
