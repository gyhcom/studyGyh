/* (C)2024 */
package com.study.gyh.settings;

import static com.study.gyh.settings.SettingsController.ZONES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.gyh.WithAccount;
import com.study.gyh.account.AccountRepository;
import com.study.gyh.account.AccountService;
import com.study.gyh.domain.Account;
import com.study.gyh.domain.Tag;
import com.study.gyh.domain.Zone;
import com.study.gyh.mail.EmailService;
import com.study.gyh.settings.form.ZoneForm;
import com.study.gyh.tag.TagForm;
import com.study.gyh.tag.TagRepository;
import com.study.gyh.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;

    // 현재 테스트 클래스에서 사용하지 않는 Bean지만
    // 넣어주지 않으면 에러가 난다.. SpringBootTest면 에러가 안나야 하는데.. 왜지.
    @MockBean EmailService emailService;

    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;
    @Autowired TagRepository tagRepository;
    @Autowired AccountService accountService;
    @Autowired private ZoneRepository zoneRepository;

    @Autowired RedisTemplate redisTemplate;

    @Autowired StringRedisTemplate stringRedisTemplate;

    @Autowired ReactiveRedisTemplate reactiveRedisTemplate;

    @Autowired ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    private Zone testZone =
            Zone.builder().city("test").localNameOfCity("테스트시").province("테스트주").build();

    @BeforeEach
    void beforeEach() {
        zoneRepository.save(testZone);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

    @WithAccount("gyh")
    @DisplayName("계정의 태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
                .andExpect(view().name(SettingsController.SETTINGS_TAGS_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @Transactional
    @WithAccount("gyh")
    @DisplayName("계정에 태그 추가")
    @Test
    void addTag() throws Exception {

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(
                        post(SettingsController.SETTINGS_TAGS_URL + "/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tagForm))
                                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account gyh = accountRepository.findByNickname("gyh");
        assertTrue(gyh.getTags().contains(newTag));
    }

    @Transactional
    @WithAccount("gyh")
    @DisplayName("계정에 태그 삭제")
    @Test
    void removeTag() throws Exception {
        // TODO: 테스트 실패 디버거 해봐라 ^_^ 그래야 실력이 늘지
        // 멍청한 짓을 했다. /remove 메소드 호출할때 ResponseEntity를 badRequest로 리턴을 줘서 안됬다..굉장히 멍청하게
        // 소스를 다시 한번 꼼꼼히 보고.. 했는데.. 간단한걸 놓쳐서 2시간 고생했다..
        Account gyh = accountRepository.findByNickname("gyh");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(gyh, newTag);

        assertTrue(gyh.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(
                        post(SettingsController.SETTINGS_TAGS_URL + "/remove")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tagForm))
                                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(gyh.getTags().contains(newTag));
    }

    @WithAccount("gyh")
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateAccount_success() throws Exception {
        String newNickname = "gyhcom";
        mockMvc.perform(
                        post(SettingsController.SETTINGS_ACCOUNT_URL)
                                .param("nickname", newNickname)
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_ACCOUNT_URL))
                .andExpect(flash().attributeExists("message"));
    }

    @WithAccount("gyh")
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateAccount_failure() throws Exception {
        String newNickname = "\\_()_/+;";
        mockMvc.perform(
                        post(SettingsController.SETTINGS_ACCOUNT_URL)
                                .param("nickname", newNickname)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("gyh")
    @DisplayName("프로필 수정하기 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("gyh")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개 수정";
        mockMvc.perform(
                        post(SettingsController.SETTINGS_PROFILE_URL)
                                .param("bio", bio)
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account gyh = accountRepository.findByNickname("gyh");
        System.out.println(gyh.toString());

        assertEquals(bio, gyh.getBio());
    }

    @WithAccount("gyh")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우, 길게 길게 소개를 수정하는 경우, 길게 길게 소개를 수정하는 경우, 길게 소개를 수정하는 경우, 길게";
        mockMvc.perform(
                        post(SettingsController.SETTINGS_PROFILE_URL)
                                .param("bio", bio)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account gyh = accountRepository.findByNickname("gyh");
        System.out.println(gyh.toString());

        assertNull(gyh.getBio());
    }

    @WithAccount("gyh")
    @DisplayName("패스워드 수정 폼")
    @Test
    void updatePassWord_Form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("gyh")
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    void updatePassWord_success() throws Exception {
        mockMvc.perform(
                        post(SettingsController.SETTINGS_PASSWORD_URL)
                                .param("newPassword", "12345678")
                                .param("newPasswordConfirm", "12345678")
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account gyh = accountRepository.findByNickname("gyh");
        assertTrue(passwordEncoder.matches("12345678", gyh.getPassword()));
    }

    @WithAccount("gyh")
    @DisplayName("패스워드 수정 - 입력값 에러")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(
                        post(SettingsController.SETTINGS_PASSWORD_URL)
                                .param("newPassword", "12345678")
                                .param("newPasswordConfirm", "11111111")
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @WithAccount("gyh")
    @DisplayName("계정의 지역 정보 수정화면")
    @Test
    void updateZonesForm() throws Exception {
        //        Zone zoneTest = new
        // Zone().builder().city("test").localNameOfCity("테스트시").province("테스트주")
        //            .build();

        ZoneForm zoneForm = new ZoneForm();
        mockMvc.perform(get("/" + ZONES))
                .andExpect(view().name(ZONES))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithAccount("gyh")
    @DisplayName("계정의 지역정보 추가")
    @Transactional
    @Test
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(
                        post("/" + ZONES + "/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(zoneForm))
                                .with(csrf()))
                .andExpect(status().isOk());

        Account gyh = accountRepository.findByNickname("gyh");
        Zone zone =
                zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertTrue(gyh.getZones().contains(zone));
    }

    @WithAccount("gyh")
    @DisplayName("계정의 지역정보 삭제")
    @Transactional
    @Test
    void removeZone() throws Exception {
        Account gyh = accountRepository.findByNickname("gyh");
        // 사용자 정보 불러옴
        Zone zone =
                zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        // 테스트 케이스 실행할때 입력한 testZone 객체 입력
        accountService.addZone(gyh, zone);
        // 사용자에게 입력해 테스트 데이터 구축

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(
                        post("/" + ZONES + "/remove")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(zoneForm))
                                // Json형식의 String으로 만들기 위해 objectMapper를 사용
                                .with(csrf()))
                // springsecurity에서는 비정상적인 요청을 관리하기 위해 csrf토큰을 사용하는데
                // 테스트에서는 이런 처리가 필용하지 않아 임의로 csrf토큰을 만들어주는 옵션
                .andExpect(status().isOk());
        // Http 200이어야  통과

        assertFalse(gyh.getZones().contains(zone));
    }

    @Test
    void Test() {
        redisTemplate.opsForValue().set("gyh", "Hello");
        System.out.println("=========" + redisTemplate.opsForValue().get("key"));
        System.out.println("=========" + stringRedisTemplate.opsForValue().get("key"));
    }
}
