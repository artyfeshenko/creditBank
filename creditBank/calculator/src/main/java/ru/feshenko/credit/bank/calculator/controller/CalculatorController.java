package ru.feshenko.credit.bank.calculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feshenko.credit.bank.calculator.dto.CreditDto;
import ru.feshenko.credit.bank.calculator.dto.ErrorResponse;
import ru.feshenko.credit.bank.calculator.dto.LoanOfferDto;
import ru.feshenko.credit.bank.calculator.dto.LoanStatementRequestDto;
import ru.feshenko.credit.bank.calculator.dto.ScoringDataDto;
import ru.feshenko.credit.bank.calculator.service.CreditService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
@Tag(name = "Credit Calculation", description = "API for calculating credit and generating loan offers")
public class CalculatorController {

    private final CreditService creditService;

    @Operation(
            summary = "Generate loan offers",
            description = "Generates a list of possible loan offers based on the initial request"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Loan offers generated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoanOfferDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> getLoanOffers(@Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        log.info("/offers input values: amount={}, term={}", loanStatementRequestDto.amount(), loanStatementRequestDto.term());
        List<LoanOfferDto> loanOfferDtoList = creditService.generateOffers(loanStatementRequestDto);
        log.info("/offers output values: {}", loanOfferDtoList);
        return ResponseEntity.ok(loanOfferDtoList);
    }

    @Operation(
            summary = "Score and calculate credit ",
            description = "Performs full credit scoring and calculates detailed credit information with payment schedule."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Credit calculated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreditDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or scoring failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/calc")
    public ResponseEntity<CreditDto> scoreAndCalculate(@RequestBody ScoringDataDto scoringDataDto) {
        log.info("calc input values: amount={}, term={}, dependentAmount={}, isInsuranceEnabled={}, isSalaryClient={}",
                scoringDataDto.amount(), scoringDataDto.term(), scoringDataDto.dependentAmount(),
                scoringDataDto.isInsuranceEnabled(), scoringDataDto.isSalaryClient());
        CreditDto creditDto = creditService.scoreAndCalculateCredit(scoringDataDto);
        log.info("calc output values: amount={}, term={}, rate={}, monthlyPayment={}",
                creditDto.amount(), creditDto.term(),
                creditDto.rate(), creditDto.monthlyPayment());
        return ResponseEntity.ok(creditDto);
    }
}
