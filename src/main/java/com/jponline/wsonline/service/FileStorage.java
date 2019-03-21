package com.jponline.wsonline.service;

import org.springframework.core.io.Resource;

public interface FileStorage {

    Resource loadFile(String filename);

}
