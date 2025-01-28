/* (C)2024 */
package com.study.gyh.study;

import com.study.gyh.account.CurrentUser;
import com.study.gyh.domain.Account;
import com.study.gyh.domain.Study;
import com.study.gyh.study.form.StudyForm;
import com.study.gyh.study.validator.StudyFormValidator;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyRepository studyRepository;
    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final StudyFormValidator studyFormValidator;

    @InitBinder("studyForm")
    public void studyFormInitBInder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyFormValidator);
    }

    @GetMapping("/new-Study")
    public String newStudForm(@CurrentUser Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute(new StudyForm());
        return "study/form";
    }

    @PostMapping("/new-study")
    public String newStudySubmit(
            @CurrentUser Account account, @Valid StudyForm studyForm, Errors errors) {
        if (errors.hasErrors()) {
            return "study/form";
        }

        Study newStudy =
                studyService.createNewStudy(modelMapper.map(studyForm, Study.class), account);
        // PostMapping redirect Prg패턴
        return "redirect:/study/" + URLEncoder.encode(newStudy.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/study/{path}")
    public String viewStudy(@CurrentUser Account account, @PathVariable String path, Model model) {
        model.addAttribute(account);
        model.addAttribute(studyRepository.findByPath(path));
        return "study/view";
    }

    @GetMapping("/study/{path}/members")
    public String viewStudyMembers(
            @CurrentUser Account account, @PathVariable String path, Model model) {
        model.addAttribute(account);
        model.addAttribute(studyRepository.findByPath(path));
        return "study/members";
    }
}
