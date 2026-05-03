package ru.feshenko.credit.bank.statement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.feshenko.credit.bank.statement.client.DealClient;
import ru.feshenko.credit.bank.statement.dto.LoanOfferDto;
import ru.feshenko.credit.bank.statement.dto.LoanStatementRequestDto;
import ru.feshenko.credit.bank.statement.service.StatementService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {

    @Mock
    private DealClient dealClient;

    @InjectMocks
    private StatementService statementService;

    private LoanStatementRequestDto createDto() {
        return new LoanStatementRequestDto(BigDecimal.valueOf(100000), 12, "leha", "petrov",
                "alexeevich", "leha@mail.com", LocalDate.of(1990, 1, 1),
                "4202", "159580");
    }

    @Test
    void generateOffers_ShouldReturnOffersFromClient() {
        LoanStatementRequestDto request = createDto();
        List<LoanOfferDto> expectedOffers = List.of(new LoanOfferDto(), new LoanOfferDto());
        when(dealClient.getLoanOffers(request)).thenReturn(expectedOffers);
        List<LoanOfferDto> actualOffers = statementService.generateOffers(request);
        assertThat(actualOffers).isSameAs(expectedOffers);
        verify(dealClient, times(1)).getLoanOffers(request);
        verifyNoMoreInteractions(dealClient);
    }

    @Test
    void generateOffers_WhenClientThrowsException_ShouldPropagateException() {
        LoanStatementRequestDto request = createDto();
        RuntimeException clientException = new RuntimeException("Deal client error");
        when(dealClient.getLoanOffers(request)).thenThrow(clientException);
        assertThatThrownBy(() -> statementService.generateOffers(request))
                .isSameAs(clientException);
        verify(dealClient, times(1)).getLoanOffers(request);
    }

    @Test
    void selectOffer_ShouldCallClientWithCorrectOffer() {
        LoanOfferDto offer = new LoanOfferDto();
        doNothing().when(dealClient).selectOffer(offer);
        statementService.selectOffer(offer);
        verify(dealClient, times(1)).selectOffer(offer);
        verifyNoMoreInteractions(dealClient);
    }

    @Test
    void selectOffer_WhenClientThrowsException_ShouldPropagateException() {
        LoanOfferDto offer = new LoanOfferDto();
        RuntimeException clientException = new RuntimeException("Selection failed");
        doThrow(clientException).when(dealClient).selectOffer(offer);

        assertThatThrownBy(() -> statementService.selectOffer(offer))
                .isSameAs(clientException);
        verify(dealClient, times(1)).selectOffer(offer);
    }
}

