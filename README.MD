## LobbyPresents
### This Code is only 1.8.8, not same as on spigot but this is the latest version

# Project Overview

I initially created this project in the long past and, for various reasons, haven't been able to update it further. Nevertheless, I welcome you to utilize it for corrections, improvements, or as a helpful guideline.

## Important Notes

- The source code may exhibit unconventional naming conventions for classes and variables, reflecting a beginner's approach at the time of creation.
- You might find code in unexpected or peculiar sections due to the necessity of decompiling it when some files were missing.

## For Mysql
- Mysql 8+

## Contributions and Fixes

Feel free to contribute to the project by fixing and improving any aspects that need attention. Your support in enhancing the code and documentation is highly appreciated!

## Developer
### Events
```java
@EventHandler
public void onPlayerClickPresents(PlayerClickClaimedPresentEvent e) {
        e.getPlayer();
        e.getID();
        e.getTotal();
        e.getProfile();
        e.getLocation();
}

public void test() {
        // getset user data
        PresentsUtils.getProfile()
}

```
