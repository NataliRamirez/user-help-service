package com.qvenly.userhelp.services;

import com.qvenly.userhelp.exceptions.BusinessException;
import com.qvenly.userhelp.exceptions.ResourceNotFoundException;
import com.qvenly.userhelp.models.dto.AuthenticatedUserDTO;
import com.qvenly.userhelp.models.dto.CreateSupportTicketDTO;
import com.qvenly.userhelp.models.dto.SupportReplyRequestDTO;
import com.qvenly.userhelp.models.dto.SupportTicketResponseDTO;
import com.qvenly.userhelp.models.entity.SupportTicket;
import com.qvenly.userhelp.models.enums.SupportStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SupportTicketService {
    private final Map<UUID, SupportTicket> tickets = new ConcurrentHashMap<>();
    private final SafetyPolicyService safetyPolicyService;

    public SupportTicketService(SafetyPolicyService safetyPolicyService) {
        this.safetyPolicyService = safetyPolicyService;
    }

    public SupportTicketResponseDTO create(CreateSupportTicketDTO request, AuthenticatedUserDTO user) {
        boolean duplicated = tickets.values().stream()
                .anyMatch(ticket -> ticket.getUserId().equals(user.id())
                        && ticket.getStatus() != SupportStatus.RESOLVED
                        && ticket.getType() == request.type()
                        && safetyPolicyService.normalize(ticket.getDescription()).equals(safetyPolicyService.normalize(request.description())));

        if (duplicated) {
            throw new BusinessException("Ya existe una solicitud de soporte activa con la misma descripción y tipo");
        }

        LocalDateTime now = LocalDateTime.now();
        SupportTicket ticket = new SupportTicket(
                UUID.randomUUID(),
                user.id(),
                user.email(),
                request.type(),
                request.description(),
                request.priority(),
                SupportStatus.PENDING,
                null,
                now,
                now
        );
        tickets.put(ticket.getId(), ticket);
        return toResponse(ticket);
    }

    public List<SupportTicketResponseDTO> findMine(AuthenticatedUserDTO user) {
        return tickets.values().stream()
                .filter(ticket -> ticket.getUserId().equals(user.id()))
                .sorted(Comparator.comparing(SupportTicket::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    public List<SupportTicketResponseDTO> findAll() {
        return tickets.values().stream()
                .sorted(Comparator.comparing(SupportTicket::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    public SupportTicketResponseDTO findById(UUID ticketId) {
        return toResponse(getTicket(ticketId));
    }

    public SupportTicketResponseDTO reply(UUID ticketId, SupportReplyRequestDTO request) {
        SupportTicket ticket = getTicket(ticketId);
        if (ticket.getStatus() == SupportStatus.RESOLVED && ticket.getAdminResponse() != null) {
            throw new BusinessException("La solicitud ya fue respondida y resuelta");
        }
        ticket.setAdminResponse(request.response());
        ticket.setStatus(SupportStatus.RESOLVED);
        ticket.setUpdatedAt(LocalDateTime.now());
        return toResponse(ticket);
    }

    private SupportTicket getTicket(UUID ticketId) {
        SupportTicket ticket = tickets.get(ticketId);
        if (ticket == null) {
            throw new ResourceNotFoundException("No se encontró la solicitud de soporte");
        }
        return ticket;
    }

    private SupportTicketResponseDTO toResponse(SupportTicket ticket) {
        return new SupportTicketResponseDTO(
                ticket.getId(),
                ticket.getUserId(),
                ticket.getUserEmail(),
                ticket.getType(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getAdminResponse(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}
