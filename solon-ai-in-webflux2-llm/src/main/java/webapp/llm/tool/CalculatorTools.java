package webapp.llm.tool;

import org.noear.solon.ai.annotation.ToolMapping;
import org.noear.solon.ai.chat.tool.AbsToolProvider;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 如果有注入字段？请使用组件注解
 * */
@Component
public class CalculatorTools extends AbsToolProvider {
    @Autowired
    CalculatorService  calculatorService; //示意一下

    @ToolMapping(description = "将两个数字相加")
    public int add(@Param int a, @Param int b) {
        return a + b;
    }

    @ToolMapping(description = "从第一个数中减去第二个数")
    public int subtract(@Param int a, @Param int b) {
        return a - b;
    }

    @ToolMapping(description = "将两个数相乘")
    public int multiply(@Param int a, @Param int b) {
        return a * b;
    }

    @ToolMapping(description = "将第一个数除以第二个数")
    public float divide(@Param float a, @Param float b) {
        return a / b;
    }
}
