# Overview

File backup software written in Java + Spring Boot and using picocli.  Provides a command-line interface and works on Windows 10.

Has the ability to copy file attributes.  Deletes extraneous files from backups.

Does not offer cloud integration, compression, encryption, or scheduling capabilities.  Does not work on Mac or Linux.

**Warning:** use the **mirror** command to perform a simple file backup.  The **start** command will delete extraneous files from backups which can result in data loss.

# Usage

`java -jar SabourCLI.jar`

```
Usage:  [COMMAND]
Commands:
  add                     Adds a backup relationship.
  stats, statistics       Displays how many files will be copied and how much
                            disk space is needed.
  mirror, copy, clone     Starts backing up files.
  delete, clean, collect  Deletes extraneous files from backups.
  remove                  Removes a backup relationship by position.
  show, list              Shows backup relationships and positions.
  start                   Combined command for stats, delete, and mirror.
  status                  Shows the date and time of the last successful backup.
```

# Examples

```
add
-original=C:\Data
-mirror="U:\Data Backup"
```

```
remove 2
```
