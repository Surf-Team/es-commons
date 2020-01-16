package ru.es.lang.limiters;

import ru.es.lang.ESGetter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SafeAuthManager
{
    TimeLimiter ipTimeLimiter;
    CountTimeLimiter wrongPassLimiter;
    Map<String, CountTimeLimiter> accountIpsLimiter = new ConcurrentHashMap<>();
    Map<String, CountTimeLimiter> ipAccountLimiter = new ConcurrentHashMap<>();


    private final int лимит_ip_для_аккаунта_минуты;
    private final int лимит_ip_для_аккаунта;

    private final int лимит_аккаунтов_с_ip_минуты;
    private final int лимит_аккаунтов_с_ip;

    public SafeAuthManager()
    {
        this(10, 10, 15,
                10, 3*60,
                30, 60);
    }

    public SafeAuthManager(int частый_вход_с_ip_секунды, int неправильный_пароль_лимит, int непр_пароль_минуты,
                           int лимит_аккаунтов_с_ip, int лимит_аккаунтов_с_ip_минуты,
                           int лимит_ip_для_аккаунта, int лимит_ip_для_аккаунта_минуты)
    {
        ipTimeLimiter = new TimeLimiter(частый_вход_с_ip_секунды * 1000);
        wrongPassLimiter = new CountTimeLimiter(непр_пароль_минуты * 60 * 1000, неправильный_пароль_лимит);
        this.лимит_ip_для_аккаунта_минуты = лимит_ip_для_аккаунта_минуты;
        this.лимит_ip_для_аккаунта = лимит_ip_для_аккаунта;
        this.лимит_аккаунтов_с_ip_минуты = лимит_аккаунтов_с_ip_минуты;
        this.лимит_аккаунтов_с_ip = лимит_аккаунтов_с_ip;
    }

    public enum Result
    {
        SUCCESS,
        СЛИШКОМ_ЧАСТЫЙ_ВХОД_С_IP,
        СЛИШКОМ_МНОГО_РАЗНЫХ_АККАУНТОВ_С_IP,
        СЛИШКОМ_МНОГО_НЕПРАВИЛЬНЫХ_ПАРОЛЕЙ_С_IP,
        НЕ_ПРАВИЛЬНЫЙ_ПАРОЛЬ,
        СЛИШКОМ_МНОГО_РАЗНЫХ_IP_НА_1_АККАУНТ
    }

    public Result result(String ip, String account, ESGetter<Boolean> authChecker)
    {
        if (!ipTimeLimiter.allow(ip))
            return Result.СЛИШКОМ_ЧАСТЫЙ_ВХОД_С_IP;



        CountTimeLimiter ctl0 = ipAccountLimiter.get(ip);
        if (ctl0 == null)
        {
            ctl0 = new CountTimeLimiter(лимит_аккаунтов_с_ip_минуты * 60 * 1000, лимит_аккаунтов_с_ip);
            ipAccountLimiter.put(ip, ctl0);
        }
        ctl0.add(account);
        if (!ctl0.allowUnique(account))
            return Result.СЛИШКОМ_МНОГО_РАЗНЫХ_АККАУНТОВ_С_IP;
        



        CountTimeLimiter ctl = accountIpsLimiter.get(account);
        if (ctl == null)
        {
            ctl = new CountTimeLimiter(лимит_ip_для_аккаунта_минуты * 60 * 1000, лимит_ip_для_аккаунта);
            accountIpsLimiter.put(account, ctl);
        }
        ctl.add(ip);
        if (!ctl.allowUnique(ip))
            return Result.СЛИШКОМ_МНОГО_РАЗНЫХ_IP_НА_1_АККАУНТ;


        if (!wrongPassLimiter.allow(ip))
            return Result.СЛИШКОМ_МНОГО_НЕПРАВИЛЬНЫХ_ПАРОЛЕЙ_С_IP;


        
        if (authChecker.get())
            return Result.SUCCESS;
        else
        {
            wrongPassLimiter.add(ip);
            return Result.НЕ_ПРАВИЛЬНЫЙ_ПАРОЛЬ;
        }
    }
}
