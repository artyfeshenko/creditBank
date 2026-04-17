package ru.feshenko.credit.bank.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.feshenko.credit.bank.calculator.dto.EmploymentDto;
import ru.feshenko.credit.bank.calculator.dto.ScoringDataDto;
import ru.feshenko.credit.bank.calculator.enums.EmploymentStatusEnum;
import ru.feshenko.credit.bank.calculator.enums.GenderEnum;
import ru.feshenko.credit.bank.calculator.enums.MaritalStatusEnum;
import ru.feshenko.credit.bank.calculator.enums.PositionEnum;
import ru.feshenko.credit.bank.calculator.exception.ScoringDataException;
import ru.feshenko.credit.bank.calculator.service.CalculatorService;
import ru.feshenko.credit.bank.calculator.service.ScoringService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScoringServiceTest {
    private ScoringService scoringService;
    private CalculatorService calculatorService;

    private ScoringDataDto buildDto() {
        return new ScoringDataDto(BigDecimal.valueOf(500000), 6, "sano", "mironov", "michailovich", GenderEnum.NON_BINARY, LocalDate.of(2003, 1, 1), "4202", "168138", LocalDate.of(2017, 2, 2), "ROMADIN MVD", MaritalStatusEnum.DIVORCED, 5,
                new EmploymentDto(EmploymentStatusEnum.SELF_EMPLOYED, "12341345", BigDecimal.valueOf(60000), PositionEnum.TOP_MANAGER, 20, 5
                ), "123456789", false, false
        );
    }

    @BeforeEach
    void setUp() {
        calculatorService = mock(CalculatorService.class);
        scoringService = new ScoringService(calculatorService);
        when(calculatorService.calculateRate(false, false)).thenReturn(BigDecimal.valueOf(15));
    }

    @Test
    void testCalculateScoringRate_success() {
        BigDecimal expectedRate = BigDecimal.valueOf(28);
        BigDecimal actual = scoringService.calculateScoringRate(buildDto());
        assertEquals(expectedRate, actual);
    }

    @Test
    void testValidateScoringDataWhenUnemployed() {
        ScoringDataDto dto = new ScoringDataDto(BigDecimal.valueOf(500000), 6, "sano", "mironov", "michailovich", GenderEnum.NON_BINARY, LocalDate.of(2003, 1, 1), "4202", "168138", LocalDate.of(2017, 2, 2), "ROMADIN MVD", MaritalStatusEnum.DIVORCED, 5,
                new EmploymentDto(EmploymentStatusEnum.UNEMPLOYED, "12341345", BigDecimal.valueOf(60000), PositionEnum.TOP_MANAGER, 20, 5
                ), "123456789", false, false
        );
        assertThrows(ScoringDataException.class, ()-> scoringService.validateScoringData(dto));
    }

    @Test
    void testValidateScoringDataWhenAgeNotSuitable() {
        ScoringDataDto dto = new ScoringDataDto(BigDecimal.valueOf(500000), 6, "sano", "mironov", "michailovich", GenderEnum.NON_BINARY, LocalDate.of(2024, 1, 1), "4202", "168138", LocalDate.of(2017, 2, 2), "ROMADIN MVD", MaritalStatusEnum.DIVORCED, 5,
                new EmploymentDto(EmploymentStatusEnum.SELF_EMPLOYED, "12341345", BigDecimal.valueOf(60000), PositionEnum.TOP_MANAGER, 20, 5
                ), "123456789", false, false
        );
        assertThrows(ScoringDataException.class, ()-> scoringService.validateScoringData(dto));
    }

    @Test
    void testValidateScoringDataWhenSalaryAmountSmall() {
        ScoringDataDto dto = new ScoringDataDto(BigDecimal.valueOf(500000), 6, "sano", "mironov", "michailovich", GenderEnum.NON_BINARY, LocalDate.of(2001, 1, 1), "4202", "168138", LocalDate.of(2017, 2, 2), "ROMADIN MVD", MaritalStatusEnum.DIVORCED, 5,
                new EmploymentDto(EmploymentStatusEnum.SELF_EMPLOYED, "12341345", BigDecimal.valueOf(5000), PositionEnum.TOP_MANAGER, 20, 5
                ), "123456789", false, false
        );
        assertThrows(ScoringDataException.class, ()-> scoringService.validateScoringData(dto));
    }

    @Test
    void testValidateScoringDataWhenTotalLengthServiceSmall() {
        ScoringDataDto dto = new ScoringDataDto(BigDecimal.valueOf(500000), 6, "sano", "mironov", "michailovich", GenderEnum.NON_BINARY, LocalDate.of(2001, 1, 1), "4202", "168138", LocalDate.of(2017, 2, 2), "ROMADIN MVD", MaritalStatusEnum.DIVORCED, 5,
                new EmploymentDto(EmploymentStatusEnum.SELF_EMPLOYED, "12341345", BigDecimal.valueOf(5000), PositionEnum.TOP_MANAGER, 15, 5
                ), "123456789", false, false
        );
        assertThrows(ScoringDataException.class, ()-> scoringService.validateScoringData(dto));
    }

    @Test
    void testValidateScoringDataWhenCurrentLengthServiceSmall() {
        ScoringDataDto dto = new ScoringDataDto(BigDecimal.valueOf(500000), 6, "sano", "mironov", "michailovich", GenderEnum.NON_BINARY, LocalDate.of(2001, 1, 1), "4202", "168138", LocalDate.of(2017, 2, 2), "ROMADIN MVD", MaritalStatusEnum.DIVORCED, 5,
                new EmploymentDto(EmploymentStatusEnum.SELF_EMPLOYED, "12341345", BigDecimal.valueOf(5000), PositionEnum.TOP_MANAGER, 20, 2
                ), "123456789", false, false
        );
        assertThrows(ScoringDataException.class, ()-> scoringService.validateScoringData(dto));
    }

    @Test
    void testEmploymentScoring_selfEmployed() {
        ScoringDataDto dto = buildDto();
        BigDecimal baseRate = BigDecimal.valueOf(15);
        BigDecimal result = scoringService.applyEmploymentScoring(dto, baseRate);
        assertEquals(BigDecimal.valueOf(17), result);
    }

    @Test
    void testEmploymentScoring_BusinessOwner() {
        ScoringDataDto dto = new ScoringDataDto(BigDecimal.valueOf(500000), 6, "sano", "mironov", "michailovich", GenderEnum.NON_BINARY, LocalDate.of(2001, 1, 1), "4202", "168138", LocalDate.of(2017, 2, 2), "ROMADIN MVD", MaritalStatusEnum.DIVORCED, 5,
                new EmploymentDto(EmploymentStatusEnum.BUSINESS_OWNER, "12341345", BigDecimal.valueOf(5000), PositionEnum.TOP_MANAGER, 20, 2
                ), "123456789", false, false
        );
        BigDecimal baseRate = BigDecimal.valueOf(15);
        BigDecimal result = scoringService.applyEmploymentScoring(dto, baseRate);
        assertEquals(BigDecimal.valueOf(16), result);
    }

    @Test
    void testPositionScoring_TopManager() {
        ScoringDataDto dto = buildDto();
        BigDecimal baseRate = BigDecimal.valueOf(15);
        BigDecimal result = scoringService.applyPositionScoring(dto, baseRate);
        assertEquals(BigDecimal.valueOf(18), result);
    }

    @Test
    void testPositionScoring_MiddleManager() {
        ScoringDataDto dto = new ScoringDataDto(BigDecimal.valueOf(500000), 6, "sano", "mironov", "michailovich", GenderEnum.NON_BINARY, LocalDate.of(2003, 1, 1), "4202", "168138", LocalDate.of(2017, 2, 2), "ROMADIN MVD", MaritalStatusEnum.DIVORCED, 5,
                new EmploymentDto(EmploymentStatusEnum.SELF_EMPLOYED, "12341345", BigDecimal.valueOf(60000), PositionEnum.MIDDLE_MANAGER, 20, 5
                ), "123456789", false, false
        );
        BigDecimal baseRate = BigDecimal.valueOf(15);
        BigDecimal result = scoringService.applyPositionScoring(dto, baseRate);
        assertEquals(BigDecimal.valueOf(17), result);
    }

    @Test
    void testMaritalStatusScoring_Married() {
        ScoringDataDto dto = new ScoringDataDto(BigDecimal.valueOf(500000), 6, "sano", "mironov", "michailovich", GenderEnum.NON_BINARY, LocalDate.of(2003, 1, 1), "4202", "168138", LocalDate.of(2017, 2, 2), "ROMADIN MVD", MaritalStatusEnum.MARRIED, 5,
                new EmploymentDto(EmploymentStatusEnum.SELF_EMPLOYED, "12341345", BigDecimal.valueOf(60000), PositionEnum.TOP_MANAGER, 20, 5
                ), "123456789", false, false
        );
        BigDecimal baseRate = BigDecimal.valueOf(15);
        BigDecimal result = scoringService.applyMaritalStatusScoring(dto, baseRate);
        assertEquals(BigDecimal.valueOf(12), result);
    }

    @Test
    void testMaritalStatusScoring_Divorced() {
        ScoringDataDto dto = buildDto();
        BigDecimal baseRate = BigDecimal.valueOf(15);
        BigDecimal result = scoringService.applyMaritalStatusScoring(dto, baseRate);
        assertEquals(BigDecimal.valueOf(16), result);
    }

    @Test
    void testGenderScoring_NonBinary() {
        ScoringDataDto dto = buildDto();
        BigDecimal baseRate = BigDecimal.valueOf(15);
        BigDecimal result = scoringService.applyGenderScoring(dto, baseRate);
        assertEquals(BigDecimal.valueOf(22), result);
    }

    @Test
    void testGenderScoring_ManInAgeRange() {
        ScoringDataDto dto = new ScoringDataDto(BigDecimal.valueOf(500000), 6, "sano", "mironov", "michailovich", GenderEnum.MALE, LocalDate.of(1990, 1, 1), "4202", "168138", LocalDate.of(2017, 2, 2), "ROMADIN MVD", MaritalStatusEnum.DIVORCED, 5,
                new EmploymentDto(EmploymentStatusEnum.SELF_EMPLOYED, "12341345", BigDecimal.valueOf(60000), PositionEnum.TOP_MANAGER, 20, 5
                ), "123456789", false, false
        );
        BigDecimal baseRate = BigDecimal.valueOf(15);
        BigDecimal result = scoringService.applyGenderScoring(dto, baseRate);
        assertEquals(BigDecimal.valueOf(12), result);
    }

    @Test
    void testGenderScoring_WomanInAgeRange() {
        ScoringDataDto dto = new ScoringDataDto(BigDecimal.valueOf(500000), 6, "sano", "mironov", "michailovich", GenderEnum.FEMALE, LocalDate.of(1990, 1, 1), "4202", "168138", LocalDate.of(2017, 2, 2), "ROMADIN MVD", MaritalStatusEnum.DIVORCED, 5,
                new EmploymentDto(EmploymentStatusEnum.SELF_EMPLOYED, "12341345", BigDecimal.valueOf(60000), PositionEnum.TOP_MANAGER, 20, 5
                ), "123456789", false, false
        );
        BigDecimal baseRate = BigDecimal.valueOf(15);
        BigDecimal result = scoringService.applyGenderScoring(dto, baseRate);
        assertEquals(BigDecimal.valueOf(12), result);
    }
}
