package com.habitrpg.android.habitica.data.implementation;

import com.habitrpg.android.habitica.data.ApiClient;
import com.habitrpg.android.habitica.data.UserRepository;
import com.habitrpg.android.habitica.data.local.UserLocalRepository;
import com.magicmicky.habitrpgwrapper.lib.models.HabitRPGUser;

import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


public class UserRepositoryImpl extends BaseRepositoryImpl<UserLocalRepository> implements UserRepository {

    public UserRepositoryImpl(UserLocalRepository localRepository, ApiClient apiClient) {
        super(localRepository, apiClient);
    }

    @Override
    public Observable<HabitRPGUser> getUser(String userID) {
        return localRepository.getUser(userID);
    }

    @Override
    public Observable<HabitRPGUser> updateUser(HabitRPGUser user, Map<String, Object> updateData) {
        return apiClient.updateUser(updateData)
                .map(newUser -> mergeUser(user, newUser));
    }

    @Override
    public Observable<HabitRPGUser> retrieveUser(Boolean withTasks) {
        return apiClient.retrieveUser(withTasks);
    }

    @Override
    public Observable<HabitRPGUser> revive(HabitRPGUser user) {
        return apiClient.revive().map(newUser -> mergeUser(user, newUser));
    }

    private HabitRPGUser mergeUser(HabitRPGUser oldUser, HabitRPGUser newUser) {
        if (newUser.getItems() != null) {
            oldUser.setItems(newUser.getItems());
        }
        if (newUser.getPreferences() != null) {
            oldUser.setPreferences(newUser.getPreferences());
        }
        if (newUser.getFlags() != null) {
            oldUser.setFlags(newUser.getFlags());
        }
        if (newUser.getStats() != null) {
            oldUser.getStats().merge(newUser.getStats());
        }

        oldUser.async().save();
        return oldUser;
    }
}