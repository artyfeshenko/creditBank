package ru.feshenko.credit.bank.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feshenko.credit.bank.deal.dto.FinishRegistrationRequestDto;
import ru.feshenko.credit.bank.deal.dto.LoanStatementRequestDto;
import ru.feshenko.credit.bank.deal.entity.Client;
import ru.feshenko.credit.bank.deal.mapper.ClientMapper;
import ru.feshenko.credit.bank.deal.repository.ClientRepository;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public Client updateAndSaveClient(Client client, FinishRegistrationRequestDto requestDto) {
        clientMapper.updateClient(client, requestDto);
        return clientRepository.save(client);
    }

    public Client createAndSaveClient(LoanStatementRequestDto dto) {
        Client client = clientMapper.toClient(dto);
        return clientRepository.save(client);
    }

}
