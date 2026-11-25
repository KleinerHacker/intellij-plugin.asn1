# Global Rules

## Concurrency

* Concurrent or long-running processes (e.g. `build`, `test`, `verifyPlugin`, `koverXmlReport`)
  MUST ALWAYS be executed through an agent (Task tool)
  * NOT through a background command of the shell
  * The agent returns the result; only the result is reported

## Limiting search

* NEVER decompile or reflect depending third party class
  * If this is required, ask the user first

## Console / CLI Output

* On Console or in CLI: MUST ALWAYS in GERMANY
* Plans printed on Console MUST ALWAYS in GERMANY

## File Output

* Into files: MUST ALWAYS in ENGLISH