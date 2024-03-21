package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysConfig;
import fun.mortnon.dal.sys.repository.ConfigRepository;
import fun.mortnon.service.sys.ConfigService;
import fun.mortnon.service.sys.EncryptService;
import fun.mortnon.service.sys.vo.SysConfigDTO;
import fun.mortnon.web.controller.system.command.UpdateConfigCommand;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2024/3/15
 */
@Singleton
@Slf4j
public class ConfigServiceImpl implements ConfigService {
    @Inject
    private ConfigRepository configRepository;

    @Inject
    private EncryptService encryptService;

    @Override
    public Mono<SysConfig> queryConfig() {
        return configRepository.findById(1L);
    }

    @Override
    public Mono<SysConfigDTO> queryLoginConfig() {
        return configRepository.findById(1L)
                .map(SysConfigDTO::convert)
                .map(dto -> {
                    if (dto.isPasswordEncrypt()) {
                        String key = encryptService.generateRSA();
                        dto.setPublicKey(key);
                    }
                    return dto;
                });
    }

    @Override
    public Mono<SysConfig> updateConfig(UpdateConfigCommand update) {
        return configRepository.findById(1L)
                .flatMap(config -> {
                    if (ObjectUtils.isNotEmpty(update.getCaptcha())) {
                        config.setCaptcha(update.getCaptcha());
                    }
                    if (ObjectUtils.isNotEmpty(update.getPasswordEncrypt())) {
                        config.setPasswordEncrypt(update.getPasswordEncrypt());
                    }
                    if (ObjectUtils.isNotEmpty(update.getDoubleFactor())) {
                        config.setDoubleFactor(update.getDoubleFactor());
                    }
                    if (ObjectUtils.isNotEmpty(update.getTryCount())) {
                        config.setTryCount(update.getTryCount());
                    }
                    if (ObjectUtils.isNotEmpty(update.getLockTime())) {
                        config.setLockTime(update.getLockTime());
                    }

                    return configRepository.update(config);
                });
    }
}
