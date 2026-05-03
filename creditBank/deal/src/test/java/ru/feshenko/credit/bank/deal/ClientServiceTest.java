package ru.feshenko.credit.bank.deal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.feshenko.credit.bank.deal.dto.EmploymentDto;
import ru.feshenko.credit.bank.deal.dto.FinishRegistrationRequestDto;
import ru.feshenko.credit.bank.deal.dto.LoanStatementRequestDto;
import ru.feshenko.credit.bank.deal.entity.Client;
import ru.feshenko.credit.bank.deal.entity.Passport;
import ru.feshenko.credit.bank.deal.enums.EmployementPosition;
import ru.feshenko.credit.bank.deal.enums.EmploymentStatus;
import ru.feshenko.credit.bank.deal.enums.Gender;
import ru.feshenko.credit.bank.deal.enums.MaritalStatus;
import ru.feshenko.credit.bank.deal.mapper.ClientMapper;
import ru.feshenko.credit.bank.deal.repository.ClientRepository;
import ru.feshenko.credit.bank.deal.service.ClientService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    private ClientService clientService;
    private ClientRepository clientRepository;
    private ClientMapper clientMapper;

    private LoanStatementRequestDto buildLoanStatementRequestDto() {
        return new LoanStatementRequestDto(
                BigDecimal.valueOf(500000),
                6,
                "sasha",
                "mironov",
                "mickhailovich",
                "sano@yandex.ru",
                LocalDate.of(2003, 11, 1),
                "4202",
                "168032"
        );
    }

    private FinishRegistrationRequestDto buildFinishRegistrationRequestDto() {
        return new FinishRegistrationRequestDto(
                Gender.MALE,
                MaritalStatus.MARRIED,
                2,
                LocalDate.of(2003, 11, 1),
                "mvd russia",
                new EmploymentDto(EmploymentStatus.SELF_EMPLOYED, "13457836423",
                        BigDecimal.valueOf(50000), EmployementPosition.WORKER, 10, 4 ),
                "40817810200000000001"
        );
    }

    private Client buildClient() {
        Client client = new Client();
        client.setFirstName("sasha");
        client.setLastName("mironov");
        client.setMiddleName("mickhailovich");
        client.setEmail("sano@yandex.ru");
        client.setBirthDate(LocalDate.of(2003, 11, 1));
        client.setPassport(new Passport(null, "2418", "159580", null, null));
        return client;
    }

    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        clientMapper = mock(ClientMapper.class);
        clientService = new ClientService(clientRepository, clientMapper);
    }

    @Test
    void testCreateAndSaveClient() {
        LoanStatementRequestDto requestDto = buildLoanStatementRequestDto();
        Client expectedClient = buildClient();

        when(clientMapper.toClient(requestDto)).thenReturn(expectedClient);

        when(clientRepository.save(expectedClient)).thenReturn(expectedClient);
        Client actualClient = clientService.createAndSaveClient(requestDto);
        assertNotNull(actualClient);
        assertEquals(expectedClient.getFirstName(), actualClient.getFirstName());
        verify(clientRepository, times(1)).save(expectedClient);
        assertNotNull(actualClient);
        assertEquals(expectedClient.getFirstName(), actualClient.getFirstName());
        assertEquals(expectedClient.getLastName(), actualClient.getLastName());
        assertEquals(expectedClient.getEmail(), actualClient.getEmail());
        verify(clientMapper, times(1)).toClient(requestDto);
    }


    @Test
    void testUpdateClient() {
        Client existingClient = buildClient();
        FinishRegistrationRequestDto requestDto = buildFinishRegistrationRequestDto();
        when(clientRepository.save(existingClient)).thenReturn(existingClient);
        doNothing().when(clientMapper).updateClient(existingClient, requestDto);
        Client updatedClient = clientService.updateAndSaveClient(existingClient, requestDto);
        assertNotNull(updatedClient);
        assertEquals(existingClient, updatedClient);
        verify(clientMapper, times(1)).updateClient(existingClient, requestDto);
    }
}
