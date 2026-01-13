# solon-ai-embedded-examples

[solon-ai & solon-ai-mcp & solon-ai-flow & solon-ai-agent](../../../../opensolon/solon-ai) 同时支持：java8、java11、java17、java21、java25（java8+）

本示例项目会经常更新，建议经常同步。

### 说明

示例内容包括：

* llm（需要修改 llm/_Constants 下的常量配置，否则启动会出错）
  * chatModel 基本使用，及单测
  * tool call 基本使用，及单测
  * rag 基本使用，及单测（embeddingModel，repository，splitter）
* mcp （含 sse, streamable 示例；支持 MCP_2025_03_26 协议）
  * server 基本使用（需要 solon 容器支持）
  * client 基本使用，及单测

### 示例

| 示例                          | 说明                |
|-----------------------------|-------------------|
| solon-ai-in-jfinal          |                   |
| solon-ai-in-jfinal-newstyle |                   |
| solon-ai-in-quarkus         |                   |
| solon-ai-in-solon           |                   |
| solon-ai-in-springboot2     |                   |
| solon-ai-in-springboot3     |                   |
| solon-ai-in-springboot4     |                   |
| solon-ai-in-springmvc       |                   |
| solon-ai-in-vertx           |                   |
|                             |                   | 
| solon-ai-in-webflux2-llm    | 只有 llm 示例（没有 mcp） |
| solon-ai-in-webflux3-llm    | 只有 llm 示例（没有 mcp） |
| solon-ai-in-webflux4-llm    | 只有 llm 示例（没有 mcp） |
