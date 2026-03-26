package ru.feshenko.credit.bank.calculator.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "loan")
@Data

public class LoanProperties {
    private BigDecimal baseRate;
    private Insurance insurance = new Insurance();
    private SalaryClient salaryClient = new SalaryClient();

    public static class Insurance {
        @Getter @Setter
        private BigDecimal price;
        @Getter @Setter
        private BigDecimal discount;
    }

    public static class SalaryClient {
        @Getter @Setter
        private BigDecimal discount;
    }
}
