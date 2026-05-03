package ru.feshenko.credit.bank.statement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feshenko.credit.bank.statement.dto.ErrorResponse;
import ru.feshenko.credit.bank.statement.dto.LoanOfferDto;
import ru.feshenko.credit.bank.statement.dto.LoanStatementRequestDto;
import ru.feshenko.credit.bank.statement.service.StatementService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/statement")
@RequiredArgsConstructor
public class StatementController {
    private final StatementService statementService;

    @Operation(
            summary = "Pre-scoring and request for loan terms",
            description = "Validates input data, performs pre-scoring, calls deal service, and returns sorted loan offers"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Loan offers successfully generated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoanOfferDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (pre-scoring failed)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Deal service unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public List <LoanOfferDto> createStatement (@RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        log.info("/offers input values: amount={}, term={}", loanStatementRequestDto.amount(), loanStatementRequestDto.term());
        List<LoanOfferDto> offers = statementService.generateOffers(loanStatementRequestDto);
        log.info("POST /statement - returned {} offers", offers.size());
        return offers;
    }

    @Operation(
            summary = "Select loan offer",
            description = "Forwards selected offer to deal service for processing with database locking"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer successfully selected"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid offer data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Statement not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Deal service unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/offer")
    public void selectOffer(@RequestBody LoanOfferDto loanOfferDto) {
        log.info("POST /statement/offer - statementId: {}", loanOfferDto.getStatementId());
        statementService.selectOffer(loanOfferDto);
        log.info("POST /statement/offer - completed for statementId: {}", loanOfferDto.getStatementId());
    }
}
