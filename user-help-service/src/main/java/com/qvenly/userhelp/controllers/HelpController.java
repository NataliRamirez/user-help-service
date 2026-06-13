package com.qvenly.userhelp.controllers;

import com.qvenly.userhelp.models.dto.AuthenticatedUserDTO;
import com.qvenly.userhelp.models.dto.HelpCategoryDTO;
import com.qvenly.userhelp.models.dto.HelpHomeResponseDTO;
import com.qvenly.userhelp.models.dto.ManualSectionResponseDTO;
import com.qvenly.userhelp.models.dto.SearchResultResponseDTO;
import com.qvenly.userhelp.services.AuthUserContextService;
import com.qvenly.userhelp.services.HelpSearchService;
import com.qvenly.userhelp.services.ManualContentService;
import com.qvenly.userhelp.services.RolePersonalizationService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/help")
public class HelpController {
    private final ManualContentService manualContentService;
    private final HelpSearchService helpSearchService;
    private final RolePersonalizationService rolePersonalizationService;
    private final AuthUserContextService authUserContextService;

    public HelpController(ManualContentService manualContentService, HelpSearchService helpSearchService,
                          RolePersonalizationService rolePersonalizationService,
                          AuthUserContextService authUserContextService) {
        this.manualContentService = manualContentService;
        this.helpSearchService = helpSearchService;
        this.rolePersonalizationService = rolePersonalizationService;
        this.authUserContextService = authUserContextService;
    }

    @GetMapping({"", "/home"})
    public HelpHomeResponseDTO home(@RequestHeader HttpHeaders headers) {
        AuthenticatedUserDTO user = authUserContextService.currentUser(headers);
        return manualContentService.home(user.role());
    }

    @GetMapping("/categories")
    public List<HelpCategoryDTO> categories(@RequestHeader HttpHeaders headers) {
        authUserContextService.currentUser(headers);
        return manualContentService.categories();
    }

    @GetMapping("/categories/{slug}")
    public HelpCategoryDTO categoryBySlug(@PathVariable String slug, @RequestHeader HttpHeaders headers) {
        authUserContextService.currentUser(headers);
        return manualContentService.categoryBySlug(slug);
    }

    @GetMapping({"/manual", "/manual/sections"})
    public List<ManualSectionResponseDTO> manual(@RequestHeader HttpHeaders headers) {
        AuthenticatedUserDTO user = authUserContextService.currentUser(headers);
        return manualContentService.findByRole(user.role());
    }

    @GetMapping("/manual/{sectionId}")
    public ManualSectionResponseDTO manualSection(@PathVariable String sectionId, @RequestHeader HttpHeaders headers) {
        authUserContextService.currentUser(headers);
        return manualContentService.findById(sectionId);
    }

    @GetMapping("/search")
    public List<SearchResultResponseDTO> search(@RequestParam String query,
                                                @RequestHeader HttpHeaders headers) {
        AuthenticatedUserDTO user = authUserContextService.currentUser(headers);
        return helpSearchService.search(query, user.role());
    }

    @GetMapping("/suggestions")
    public List<String> suggestions(@RequestHeader HttpHeaders headers) {
        AuthenticatedUserDTO user = authUserContextService.currentUser(headers);
        return rolePersonalizationService.suggestionsFor(user.role());
    }
}
