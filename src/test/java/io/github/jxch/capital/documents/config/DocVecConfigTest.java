package io.github.jxch.capital.documents.config;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class DocVecConfigTest {
    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private OllamaChatClient chatClient;

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

    @Test
    void similaritySearch() {
        List<Document> results = vectorStore.similaritySearch("ShardCollection 创建索引");
        log.info("{}", JSON.toJSONString(results));
    }

    @Test
    void ollama() {
        ChatResponse response = chatClient.call(
                new Prompt(
                        "Generate the names of 5 famous pirates.",
                        OllamaOptions.create()
                                .withModel("llama3")
                                .withTemperature(0.4f)
                ));
        log.info("{}", response);
    }

    @Test
    void ollamaStream() {
        Flux<ChatResponse> responseFlux = chatClient.stream(new Prompt("""
                Please translate the input below into English, then use this English text as a new input and answer it : "怎么做蛋炒饭?"
                """,
                OllamaOptions.create()
                        .withModel("llama3")
                        .withTemperature(0.4f)
        ));
        responseFlux.doOnNext(chatResponse -> System.out.print(chatResponse.getResults().stream().map(res -> res.getOutput().getContent()).collect(Collectors.joining())))
                .doOnComplete(() -> System.out.print("\n"))
                .doOnError(Throwable::printStackTrace)
                .blockLast();
    }

}