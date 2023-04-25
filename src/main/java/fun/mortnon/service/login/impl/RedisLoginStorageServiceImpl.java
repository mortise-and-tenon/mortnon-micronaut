package fun.mortnon.service.login.impl;

import fun.mortnon.service.login.LoginStorageService;
import fun.mortnon.service.login.enums.LoginConstants;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;

import static fun.mortnon.service.login.enums.LoginConstants.LOGIN_TOKEN;
import static fun.mortnon.service.login.enums.LoginConstants.VERIFY_CODE;

/**
 * @author dev2007
 * @date 2023/2/10
 */
@Singleton
@Named(LoginConstants.REDIS)
public class RedisLoginStorageServiceImpl implements LoginStorageService {
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> commands;

    private static final String COMMAND_RESULT_OK = "OK";

    @Inject
    public RedisLoginStorageServiceImpl(StatefulRedisConnection<String, String> connection) {
        this.connection = connection;
        this.commands = connection.sync();
    }

    @Override
    public boolean tokenIsExists(String token) {
        String userToken = commands.get(String.format(LOGIN_TOKEN, token));
        return StringUtils.isNotEmpty(userToken);
    }

    @Override
    public boolean saveToken(String token, int expiresSecond) {
        if (COMMAND_RESULT_OK.equalsIgnoreCase(commands.set(String.format(LOGIN_TOKEN, token), token))) {
            return commands.expire(String.format(LOGIN_TOKEN, token), Duration.ofSeconds(expiresSecond));
        }

        return false;
    }

    @Override
    public void deleteToken(String token) {
        commands.del(String.format(LOGIN_TOKEN, token));
    }

    @Override
    public boolean saveVerifyCode(String key, String code, long expiresSecond) {
        if (COMMAND_RESULT_OK.equalsIgnoreCase(commands.set(String.format(VERIFY_CODE, key), code))) {
            return commands.expire(String.format(VERIFY_CODE, key), Duration.ofSeconds(expiresSecond));
        }
        return false;
    }

    @Override
    public String getVerifyCode(String key) {
        return commands.get(String.format(VERIFY_CODE, key));
    }

    @Override
    public boolean deleteVerifyCode(String key) {
        return commands.del(String.format(VERIFY_CODE, key)) == 1;
    }

    @Override
    public String type() {
        return LoginConstants.REDIS;
    }
}
