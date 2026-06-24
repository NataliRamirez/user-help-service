package com.qvenly.userhelp.services;

import com.qvenly.userhelp.exceptions.BusinessException;
import com.qvenly.userhelp.exceptions.ResourceNotFoundException;
import com.qvenly.userhelp.models.dto.*;
import com.qvenly.userhelp.models.enums.Role;
import com.qvenly.userhelp.models.enums.SupportPriority;
import com.qvenly.userhelp.models.enums.SupportStatus;
import com.qvenly.userhelp.models.enums.SupportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupportTicketServiceTest {

    @Mock
    private SafetyPolicyService safetyPolicyService;

    private SupportTicketService service;
    private AuthenticatedUserDTO user;
    private AuthenticatedUserDTO admin;

    @BeforeEach
    void setUp() {
        service = new SupportTicketService(safetyPolicyService);
        user = new AuthenticatedUserDTO("user-1", "user@test.com", Role.USER);
        admin = new AuthenticatedUserDTO("admin-1", "admin@test.com", Role.ADMIN);

        lenient().when(safetyPolicyService.normalize(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0).toString().toLowerCase().trim());
    }

    @Test
    @DisplayName("Debe crear un ticket correctamente")
    void createTicket() {
        var request = new CreateSupportTicketDTO(SupportType.TECHNICAL, "No puedo iniciar sesión", SupportPriority.HIGH);
        var result = service.create(request, user);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.type()).isEqualTo(SupportType.TECHNICAL);
        assertThat(result.description()).isEqualTo("No puedo iniciar sesión");
        assertThat(result.status()).isEqualTo(SupportStatus.PENDING);
        assertThat(result.userId()).isEqualTo("user-1");
        assertThat(result.adminResponse()).isNull();
    }

    @Test
    @DisplayName("Debe rechazar tickets duplicados")
    void createDuplicateTicket() {
        var request = new CreateSupportTicketDTO(SupportType.TECHNICAL, "Problema duplicado", SupportPriority.MEDIUM);
        service.create(request, user);

        assertThatThrownBy(() -> service.create(request, user))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe una solicitud");
    }

    @Test
    @DisplayName("findMine debe devolver solo los tickets del usuario")
    void findMine() {
        var user2 = new AuthenticatedUserDTO("user-2", "other@test.com", Role.USER);

        service.create(new CreateSupportTicketDTO(SupportType.ACCOUNT, "Ticket de user1", SupportPriority.LOW), user);
        service.create(new CreateSupportTicketDTO(SupportType.ACCOUNT, "Otro de user1", SupportPriority.LOW), user);
        service.create(new CreateSupportTicketDTO(SupportType.PLAN, "Ticket de user2", SupportPriority.LOW), user2);

        var mine = service.findMine(user);
        assertThat(mine).hasSize(2);

        var otherMine = service.findMine(user2);
        assertThat(otherMine).hasSize(1);
    }

    @Test
    @DisplayName("Debe responder un ticket y registrar la respuesta en el historial")
    void reply() {
        var created = service.create(
                new CreateSupportTicketDTO(SupportType.TECHNICAL, "Ayuda con evento", SupportPriority.HIGH), user);

        var replyRequest = new SupportReplyRequestDTO("Hemos solucionado tu problema");
        var replied = service.reply(created.id(), replyRequest);

        assertThat(replied.status()).isEqualTo(SupportStatus.RESOLVED);
        assertThat(replied.adminResponse()).isEqualTo("Hemos solucionado tu problema");
    }

    @Test
    @DisplayName("No debe permitir responder un ticket ya resuelto")
    void replyAlreadyResolved() {
        var created = service.create(
                new CreateSupportTicketDTO(SupportType.ACCOUNT, "Otro problema", SupportPriority.MEDIUM), user);
        service.reply(created.id(), new SupportReplyRequestDTO("Primera respuesta"));

        assertThatThrownBy(() -> service.reply(created.id(), new SupportReplyRequestDTO("Segunda respuesta")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya fue respondida");
    }

    @Test
    @DisplayName("getResponses debe devolver el historial de respuestas al dueño del ticket")
    void getResponsesAsOwner() {
        var ticket = service.create(
                new CreateSupportTicketDTO(SupportType.EVENT, "Test respuestas", SupportPriority.LOW), user);
        service.reply(ticket.id(), new SupportReplyRequestDTO("Nuestra respuesta oficial"));

        var actualResponses = service.getResponses(ticket.id(), user);
        assertThat(actualResponses).hasSize(1);
        assertThat(actualResponses.getFirst().message()).isEqualTo("Nuestra respuesta oficial");
        assertThat(actualResponses.getFirst().respondedBy()).isEqualTo("admin");
        assertThat(actualResponses.getFirst().respondedAt()).isNotNull();
    }

    @Test
    @DisplayName("getResponses debe permitir acceso a ADMIN aunque no sea el dueño")
    void getResponsesAsAdmin() {
        var ticket = service.create(
                new CreateSupportTicketDTO(SupportType.PLAN, "Consulta de plan", SupportPriority.LOW), user);
        service.reply(ticket.id(), new SupportReplyRequestDTO("Respuesta del admin"));

        var responses = service.getResponses(ticket.id(), admin);
        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("getResponses debe denegar acceso a otro usuario que no sea el dueño")
    void getResponsesAsOtherUser() {
        var otherUser = new AuthenticatedUserDTO("user-3", "other@test.com", Role.USER);
        var ticket = service.create(
                new CreateSupportTicketDTO(SupportType.ACCOUNT, "Mi problema privado", SupportPriority.HIGH), user);

        assertThatThrownBy(() -> service.getResponses(ticket.id(), otherUser))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No tienes permiso");
    }

    @Test
    @DisplayName("getResponses debe lanzar excepción si el ticket no existe")
    void getResponsesTicketNotFound() {
        assertThatThrownBy(() -> service.getResponses(UUID.randomUUID(), user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró");
    }

    @Test
    @DisplayName("findAll debe devolver todos los tickets")
    void findAll() {
        service.create(new CreateSupportTicketDTO(SupportType.TECHNICAL, "Ticket A", SupportPriority.LOW), user);
        service.create(new CreateSupportTicketDTO(SupportType.ACCOUNT, "Ticket B", SupportPriority.LOW), user);

        var all = service.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    @DisplayName("findById debe devolver un ticket por ID")
    void findById() {
        var created = service.create(
                new CreateSupportTicketDTO(SupportType.OTHER, "Test findById", SupportPriority.LOW), user);

        var found = service.findById(created.id());
        assertThat(found).isNotNull();
        assertThat(found.id()).isEqualTo(created.id());
    }

    @Test
    @DisplayName("findById debe lanzar excepción si el ticket no existe")
    void findByIdNotFound() {
        assertThatThrownBy(() -> service.findById(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
