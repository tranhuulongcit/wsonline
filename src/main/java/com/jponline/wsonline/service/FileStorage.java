package com.jponline.wsonline.service;

import org.springframework.core.io.Resource;

/**
 * FileStorage service handle file storage
 * Author: LongTH10
 */
public interface FileStorage {

    Resource loadFile(String filename);

}
