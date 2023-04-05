# main/critica-backend Critic Backend API

### Contents

- [Commit order](https://critic.jetbrains.space/p/main/repositories/critica-backend/files/README.md#Commit-order)
- [Branch order](https://critic.jetbrains.space/p/main/repositories/critica-backend/files/README.md#Branch-order)
- [Version order](https://critic.jetbrains.space/p/main/repositories/critica-backend/files/README.md#Version-order)

### Labels

|      Label      |       Description        | CodeColor |
| :-------------: |:------------------------:| :-------: |
|       bug       |         bug case         |  #d73a4a  |
|      defer      |     pending approval     |  #cfd3d7  |
|      docs       |   documentation update   |  #D4C5F9  |
|      feat       |       feature case       |  #0075ca  |
| need discussion |    pending discussion    |  #8A4B08  |
|   performance   | performance-related case |  #d876e3  |
|    refactor     |     refactoring case     |  #008672  |
|     setting     |     settings update      |  #F9D0C4  |
|      test       |       tests update       |  #FBCA04  |

## Commit order

#### Commit order
```
CommitType: (#IssueNumber) Subject
```

### CommitTypes

|CommitType|                                Description                                 |
|:--------:|:--------------------------------------------------------------------------:|
| add |                                Adding files                                |
| feat |                   Adding functionality to existing files                   |
| ci |                 Change ci  configuration files and scripts                 |
| chore |                     Simple operations, package updates                     |
| refactor |          Changes to code without breaking existing functionality           |
| docs |                        Document creation and change                        |
| style | Changes that do not affect the code, such as class names and method names. |
| build |            Changes that affect system or external dependencies             |
| fix |                                 Bug fixes                                  |
| test |                      Writing and modifying test cases                      |
| revert |                             Reverting commits                              |
| perf |                          Performance improvements                          |
| hotfix |                                Urgent fixes                                |

### IssueNumber

> Be sure to list the issue number of the work.
>
> For hotfixes that did not create an issue, you do not have to list an issue.

### Subject

> Write job descriptions in a retrospective way.

```
~ If added smth,  `added *functionality*`
```

## Branch order

> Github Flow.

```
{BranchType}/{IssueNumber}-{WorkSummary}
```

### BranchTypes

|BranchType|            Description             |
|:--------:|:----------------------------------:|
| feature |          Adding features           |
| refactor |   Refactoring existing features    |
| hotfix |             Urgent fix             |
| chore |   Small changes, package updates   |
| docs |       Documentation updates        |
| performance |        Performance updates         |
| build | System or dependency build changes |
| fix |               Fixes                |
| test |     Tests update and addition      |

### IssueNumber

> According to issue manager.

### WorkSummary

>Briefly summarize the branch description.
>
>Use a hyphen (-) instead of space and write in lowercase English.

## Version order

<img width=520 src="https://user-images.githubusercontent.com/67373938/119933978-0ac15300-bfc0-11eb-99cd-0198b1ee6f2d.png">

> All release versions start at 01.00.00.

```
"01.01.09" becomes "1.1.9"

"01.01.10" becomes "1.1.10"
```

- Increment the **Major Version** when an API changes break backward compatibility.
- Increment the  **Minor Version** when adding new features while being compatible with the previous version.
- Increment **Patches** when minor bugs or changes such as internal code supplementation occur



## Getting Started

Download links:

SSH clone URL: ssh://git@git.jetbrains.space/critic/main/critica-backend.git

HTTPS clone URL: https://git.jetbrains.space/critic/main/critica-backend.git



These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

## Prerequisites

What things you need to install the software and how to install them.

```
Examples
```

## Deployment

Add additional notes about how to deploy this on a production system.

## Resources

Add links to external resources for this project, such as CI server, bug tracker, etc.
