package ru.feshenko.credit.bank.deal.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feshenko.credit.bank.deal.dto.ErrorResponse;
import ru.feshenko.credit.bank.deal.dto.FinishRegistrationRequestDto;
import ru.feshenko.credit.bank.deal.dto.LoanOfferDto;
import ru.feshenko.credit.bank.deal.dto.LoanStatementRequestDto;
import ru.feshenko.credit.bank.deal.service.DealService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
@Tag(name = "Deal Controller", description = "Manage credit applications: creation, offer selection, and full credit calculation")
public class DealController {

    private final DealService dealService;

    @Operation(
            summary = "Create loan application and generate offers",
            description = "Creates a new loan application (statement), saves client data, and returns available loan offers"
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
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/statement")
    public List<LoanOfferDto> calculateLoanTerms(@RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        log.info("/offers input values: amount={}, term={}", loanStatementRequestDto.amount(), loanStatementRequestDto.term());
        List<LoanOfferDto> response = dealService.generateLoanOffers(loanStatementRequestDto);
        log.info("POST /deal/statement - response: {} offers", response.size());
        return response;
    }

    @Operation(
            summary = "Select loan offer",
            description = "Applies selected loan offer to an existing loan application and updates its status"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer successfully selected"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Statement not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/offer/select")
    public void selectOffer(@RequestBody  LoanOfferDto loanOfferDto) {
        log.info("POST /deal/offer/select - request: {}", loanOfferDto);
        dealService.selectLoanOffer(loanOfferDto);
        log.info("POST /deal/offer/select - completed for statementId: {}", loanOfferDto.getStatementId());
    }


    @Operation(
            summary = "Complete registration and calculate credit",
            description = "Finalizes client registration, performs scoring, calculates credit, and updates application status"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Credit successfully calculated and application completed"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid registration data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Statement not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/calculate/{statementId}")
    public void fullLoanCalculation(@RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto, @PathVariable UUID statementId) {
        log.info("POST /deal/calculate/{} - request: {}", statementId, finishRegistrationRequestDto);
        dealService.completeRegistrationAndCalculateCredit(finishRegistrationRequestDto, statementId);
        log.info("POST /deal/calculate/{} - completed", statementId);
    }
}
