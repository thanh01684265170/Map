package hvcnbcvt_uddd.googleapi.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.User;

@Dao
public interface LoginDao {

    @Query("SELECT * FROM user_login_table")
    List<User> getUserLogin();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void saveUserLogin(User user);
}
