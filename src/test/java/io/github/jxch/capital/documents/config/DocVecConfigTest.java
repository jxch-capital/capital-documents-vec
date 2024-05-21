package io.github.jxch.capital.documents.config;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class DocVecConfigTest {
    @Autowired
    private VectorStore vectorStore;

    @Test
    void chromaVectorStore() {
        List<Document> documents = List.of(
                new Document("""
                        
                        - 分片键（ShardKey）的约束：ShardKey 必须是一个索引
                        	- 非空集合须在 ShardCollection 前创建索引
                        	- 空集合 ShardCollection 自动创建索引
                        	- ShardKey 大小无限制
                        	- 支持复合哈希分片键
                        	- Document 中可以不包含 ShardKey，插入时被当 做 Null 处理
                        	- 为 ShardKey 添加后缀 refineCollectionShardKey 命令，可以修改 ShardKey 包含的 Field
                        	- 如果 ShardKey 为非 `_ID` 字段， 那么可以修改 ShardKey 对应的值
                        
                        """)
        );
        vectorStore.add(documents);
        List<Document> results = vectorStore.similaritySearch("分片键都有哪些");
        log.info("{}", JSON.toJSONString(results));
    }

}