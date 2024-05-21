package io.github.jxch.capital.documents.config;

import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.ai.chroma.ChromaApi;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.transformers.TransformersEmbeddingClient;
import org.springframework.ai.vectorsore.ChromaVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Map;

@Data
@Configuration
public class DocVecConfig {
    public final static String DOC_VEC_EMBEDDING_CLIENT = "DOC_VEC_EMBEDDING_CLIENT";
    @Value("${doc-vec.onnx-tokenizer:onnx_tokenizer.json}")
    private String onnxTokenizer;
    @Value("${doc-vec.onnx-model:model.onnx}")
    private String onnxModel;
    @Value("${doc-vec.onnx-tmp:tmp}")
    private String onnxTmp;
    @Value("${doc-vec.collection:collection}")
    private String collection;

    @SneakyThrows
    @Bean(DOC_VEC_EMBEDDING_CLIENT)
    public TransformersEmbeddingClient embeddingClient() {
        TransformersEmbeddingClient embeddingClient = new TransformersEmbeddingClient();
        embeddingClient.setTokenizerResource(Path.of(onnxTokenizer).toUri().toURL().toExternalForm());
        embeddingClient.setModelResource(Path.of(onnxModel).toUri().toURL().toExternalForm());
        embeddingClient.setResourceCacheDirectory(Path.of(onnxTmp).toFile().getAbsolutePath());
        embeddingClient.setTokenizerOptions(Map.of("padding", "true"));
        embeddingClient.afterPropertiesSet();
        return embeddingClient;
    }

    @Bean
    public VectorStore chromaVectorStore(@Qualifier(DOC_VEC_EMBEDDING_CLIENT) EmbeddingClient embeddingClient, ChromaApi chromaApi) {
        return new ChromaVectorStore(embeddingClient, chromaApi, collection);
    }

}
