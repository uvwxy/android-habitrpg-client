android-habitrpg-client
=======================

Unofficial HabitRPG native Android Client with Widget

# Current Status

At the moment this client is borked, i.e. it only works with the old HabitRPG API v1.
You can download the app from the [Play Store](https://play.google.com/store/apps/details?id=de.uvwxy.habitrpg)

# Build

The client depends on Android_PaintBox

```
git clone git@bitbucket.org:uvwxy/paintbox.git Android_PaintBox
```

Add this library to your Eclipse workspace and refresh your project. It should find the referenced project (Check it under "Project" -> Preferences -> Android -> "Libraries")


# TODO

- model the classes exposed by the API v2 using [GSON](https://sites.google.com/site/gson/gson-user-guide#TOC-Object-Examples)
- character rendering update to all new sprites
- include new/updates features (tavern, ...)
- include CardsUI (will be released on GitHub soon)

# Old Repo

The old repository is [here](https://bitbucket.org/uvwxy/android-habitrpg-client/).
I moved it to github due to the following [request}(https://bitbucket.org/uvwxy/android-habitrpg-client/issue/8/thinking-of-forking-updating-to-apiv2-and)
and I also assume it might get more attention on GitHub.
