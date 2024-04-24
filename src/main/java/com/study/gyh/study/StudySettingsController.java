/* (C)2024 */
package com.study.gyh.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.gyh.account.CurrentUser;
import com.study.gyh.domain.Account;
import com.study.gyh.domain.Study;
import com.study.gyh.domain.Tag;
import com.study.gyh.domain.Zone;
import com.study.gyh.tag.TagForm;
import com.study.gyh.study.form.StudyDescriptionForm;
import com.study.gyh.tag.TagRepository;
import com.study.gyh.tag.TagService;
import com.study.gyh.zone.ZoneForm;
import com.study.gyh.zone.ZoneRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingsController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final ObjectMapper objectMapper;
    private final ZoneRepository zoneRepository;

    @GetMapping("/description")
    public String viewStudySetting(
            @CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study, StudyDescriptionForm.class));
        return "study/settings/description";
    }

    @PostMapping("/description")
    public String updateStudyInfo(@CurrentUser Account account, @PathVariable String path,
        @Valid StudyDescriptionForm studyDescriptionForm, Errors errors, Model model,
        RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "study/settings/description";
        }

        studyService.updateStudyDescription(study, studyDescriptionForm);
        attributes.addFlashAttribute("message", "스터디 소개를 수정했습니다.");
        return "redirect:/study/" + getPath(path) + "/settings/description";

    }

    @GetMapping("/banner")
    public String studyImageForm(
        @CurrentUser Account account, @PathVariable String path, Model model) {

        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/settings/banner";
    }

    @PostMapping("/banner")
    public String studyImageSubmit(
            @CurrentUser Account account,
            @PathVariable String path,
            String image,
            RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.updateSstudyImage(study, image);
        attributes.addFlashAttribute("message", "스터디 이미지를 수정했습니다.");
        return "redirect:/study/" + getPath(path) + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableStudyBanner(@CurrentUser Account account, @PathVariable String path) {

        Study study = studyService.getStudyToUpdate(account, path);
        studyService.enableStudybanner(study);
        return "redirect:/study/" + getPath(path) + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableStudyBanner(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.disableStudybanner(study);
        return "redirect:/study/" + getPath(path) + "/settings/banner";
    }

    @GetMapping("/tags")
    public String studyTagsForm(@CurrentUser Account account, @PathVariable String path,
        Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);

        model.addAttribute("tags",
            study.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));

        List<String> allTagTitles = tagRepository.findAll().stream().map(Tag::getTitle).collect(
            Collectors.toList());

        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTagTitles));

        return "study/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account, @PathVariable String path,
        @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdate(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        studyService.addTag(study, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @PathVariable String path,
        @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdate(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        studyService.removeTag(study, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String studyZonesForm(@CurrentUser Account account, @PathVariable String path, Model model)
        throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("zones", study.getZones().stream()
            .map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones_lamda = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());

        List<Zone> allZones = zoneRepository.findAll();
        List<String> list = new ArrayList<>();


        for (Zone s : allZones) {
            list.add(s.toString());
        }



        model.addAttribute("whitelist", objectMapper.writeValueAsString(list));
        return "study/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentUser Account account, @PathVariable String path,
        @RequestBody ZoneForm zoneForm) {
        Study study = studyService.getStudyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(),
            zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        studyService.addZone(study, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentUser Account account, @PathVariable String path,
        @RequestBody ZoneForm zoneForm) {
        Study study = studyService.getStudyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(),
            zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        studyService.removeZone(study, zone);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/study")
    public String studySettingForm(@CurrentUser Account account, @PathVariable String path,
        Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/settings/study";
    }

    private String getPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

}
