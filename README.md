# Earth2FA

Earth2FA is a Spigot API based plugin designed to allow for 2 Factor
Authentication (otherwise known as TOTP, or the Time-Based One-Time Password
Algorithm ([RFC6238](https://tools.ietf.org/html/rfc6238)) for all players on a server to optionally use, with configurable 
enforcement for certain players through permission nodes. This is designed to
allow for more secure minecraft server environments, especially when account
cracking and hacking are so prevalent. This plugin reduces the risk of a 
compromised staff account greatly, making it much harder to compromise a 
staff account and compromise a server in general.

## Popular 2FA (TOTP) Apps by Mobile Device Operating System

### Windows Phone

+ [Microsoft Authenticator](https://www.microsoft.com/en-us/p/microsoft-authenticator/9nblgggzmcj6)
+ [Authenticator](https://www.microsoft.com/store/apps/authenticator/9wzdncrfj3rj)

### Android

+ [Authy](https://play.google.com/store/apps/details?id=com.authy.authy&hl=en)
+ [Google Authenticator](https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2&hl=en)
+ [Microsoft Authenticator](https://play.google.com/store/apps/details?id=com.azure.authenticator)

### iOS

+ [Authy](https://itunes.apple.com/us/app/authy/id494168017?mt=8)
+ [Google Authenticator](https://itunes.apple.com/us/app/google-authenticator/id388497605?mt=8)
+ [Microsoft Authenticator](https://apps.apple.com/us/app/microsoft-authenticator/id983156458)

## Installation

To install Earth2FA, you simply put the `Earth2FA-V#.#.##.jar` within
the `plugins/` folder of your Bukkit/Spigot/Paper/Tuinity/Purpur server.

### Compatibility

Earth2FA is tested with Spigot 1.16.5, on Java 16. However, Earth2FA
is compatible with versions greater than Spigot 1.16.5.

### Commands

Earth2FA used 1 root command, /2fa (with aliases /auth, /authenticator,
/earthauth, /earthauthenticator, /earth2fa, and /e2fa) with a set of sub commands:

+ /2fa register - Registers the user to the authenticator.
+ /2fa connect \[6-digit-code\] - Connects the player to the authenticator.
+ /2fa unregister - Unregisters the user from the authenticator.
+ /2fa reload - Reloads the language files of the plugin.
+ /2fa reset \[player\] - Resets or unregisters the target user from the authenticator.  (Console Only)

## Permissions

Earth2FA used the root permission `earth2fa`, with a set of sub
permissions to control the behavior of various aspects of the plugin.

### Command controlling permissions
+ `earth2fa.use` (default: op) - Allows for the root use of the /2fa
  command. in order to do anything with the /2fa command, the player __must__
  have this permission.
+ `earth2fa.auth` (default: op) - Allows the user to do /2fa registerand connect.
+ `earth2fa.unregister` (default: op) - Allows the user to do /2fa unregister.
+ `earth2fa.reload` (default: op) - Allows the user to do /2fa reload.

### Messages

Here you can find all the information that is need in order to configure
messages of the plugin.

You can edit every message that you want that is inside the language files.
Most of the messages are configured (about 99% of them), and you can edit
them however you want. Some of them has built-in laceholders for displaying
information about the action that was done.

For example, the player that did the action, the target of the action, etc.
In addition to the built-in placeholders, all of the messages support placeholders
Furthermore, you can make more complex messages. For example, clickable messages
with hoverable text and such.

+ Raw Messages
Raw messages are messages taht only have a message without any extras - not
clickable, not sent as an action bar or anything else. These messages are very
simple to be editted. All you have to do is just to edit the message as a
string, and that's it!

```yml
RAW_MESSAGE: 'I am a raw message without any extras! &aColors are also supported! &{HEX:4e87ee}Even hex colors in 1.16 are supported!'
NEW_LINE_MESSAGE: |
  &aThis is the first line.
  &6This is the second line.
  &cYou can add unlimited lines :D
```

+ Complex Messages
Complex messages are messages that can have extra actions to them. Action bars, titles,
hoverable text and such, are all in this category.

+ Action Bars
You can send action bars, the messages above the hotbar, by using the following format:

```yml
MESSAGE:
  action-bar:
    text: '&aThis will be sent as an action bar!'
```

+ Titles
You can send titles, the big message in the middle of the screen, by using the
following format:

```yml
MESSAGE:
  title:
    title: '&aThe bigger text'  # If you don't want that to be sent, set this section to ''.
    sub-title: '&6The smaller text'  # If you don't want that to be sent, set this section to ''.
    fade-in: 20  # Fade in duration (in ticks).
    duration: 60  # Message duration (in ticks).
    fade-out: 20  # Fade out duration (in ticks).
```

+ Interactable Messages
You can send interactable messages that can execute commands or have hoverable
text, by using the following format:

```yml
MESSAGE:
  a:   # Random, but unique key.
    text: '&aI am hoverable text.'
    tooltip: '&6Hidden message!'
  b:
    text: '&6 I can execute commands, and I will be after the first message.'
    command: '/gmc'
```

+ Custom language file
Creating a new language file is very easy task to do. All you need to do
is to copy the en-US.yml file, rename it with a valid language format,
and that's it! You can find a list of available language formats [here](https://github.com/ItchyDitchy/Earth2FA/tree/main/src/main/resources/lang).
After you have the new file, you can edit it with the same technics that
are explained above. When a new version comes out with new messages,
your custom file will be updated automatically with the new messages,
but in English.