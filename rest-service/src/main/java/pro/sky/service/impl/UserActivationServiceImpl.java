package pro.sky.service.impl;

import org.springframework.stereotype.Service;
import pro.sky.CryptoTool;
import pro.sky.dao.AppUserDAO;
import pro.sky.entity.AppUser;
import pro.sky.service.UserActivationService;

import java.util.Optional;

@Service
public class UserActivationServiceImpl implements UserActivationService {
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    public UserActivationServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public boolean activation(String cryptoUserId) {
        Long userId = cryptoTool.idOf(cryptoUserId);
        Optional<AppUser> appUser = appUserDAO.findById(userId);
        if(appUser.isPresent()){
            AppUser user = appUser.get();
            user.setIsActive(true);
            appUserDAO.save(user);
            return  true;
        }
        return false;
    }
}
