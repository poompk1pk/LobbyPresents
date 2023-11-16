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

## Commands
1. `/lps help {page}`
2. `/lps enable/disable`
3. `/lps list`
4. `/lps tp {id}`
5. `/lps set {id}`
6. `/lps remove {id}`
7. `/lps reward settype {sequence/custom/both}`
8. `/lps reward setsequence {1-total} {none/cmd1, cmd2, ...}`
9. `/lps reward setcustom {id_presents} {none/cmd1, cmd2, ...}`
10. `/lps heads`
11. `/lps clearuserdata {playername}`
12. `/lps clearalldata confirm`
13. `/lps save`
14. `/lps actionbar addworld/removeworld`
15. `/lps actionbar enable/disable`
16. `/lps reload`
17. `/lps checkversion`


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

public void test(UUID uuid) {
        // getset user data
        PresentsUtils.getProfile(uuid);
}

```
