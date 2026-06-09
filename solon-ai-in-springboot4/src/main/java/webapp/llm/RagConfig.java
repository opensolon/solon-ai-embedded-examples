package webapp.llm;

import org.noear.solon.ai.embedding.EmbeddingModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.repository.InMemoryRepository;
import org.noear.solon.ai.rag.splitter.RegexTextSplitter;
import org.noear.solon.ai.rag.splitter.SplitterPipeline;
import org.noear.solon.ai.rag.splitter.TokenSizeTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RagConfig {
    //适量嵌入模型
    @Bean
    public EmbeddingModel embeddingModel() {
        return EmbeddingModel.of(_Constants.embedding_apiUrl)
                .provider(_Constants.embedding_provider)
                .model(_Constants.embedding_model)
                .build();
    }

    //知识库
    @Bean
    public RepositoryStorable repository(EmbeddingModel embeddingModel) {
        return new InMemoryRepository(embeddingModel);
    }

    //知识库初始化
    @Bean
    public RagConfig initRepository(RepositoryStorable storable) throws Exception {
        //示例文本
        String text = "Solon 框架由杭州无耳科技有限公司（下属 Noear 团队）开发并开源。是新一代，Java 企业级应用开发框架。从零开始构建，有自主的标准规范与开放生态。近16万行代码。\n" +
                "\n" +
                "追求： 快速、小巧、简洁\n" +
                "提倡： 克制、高效、开放";

        //示例切割
        List<Document> documents = new SplitterPipeline()
                .next(new RegexTextSplitter("\n"))
                .next(new TokenSizeTextSplitter(500))
                .split(text);

        //插入知识库
        storable.save(documents);

        return this;
    }
}
