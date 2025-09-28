package data.dataProvider;

import org.testng.annotations.DataProvider;

public class UserTestData {
    @DataProvider(name = "Stubs")
    public static Object[][] UsersStubs() {
        return new Object[][] {
                {"/users", 200},
                {"/users?error=true", 500}
        };
    }
}
