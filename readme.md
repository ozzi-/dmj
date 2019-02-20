# dmj - dump mail java
This tool will enable you to search for a specific keyword in the subjects of mails in a inbox.
If the keyword is present in a subject, said mail will be dumped to the filesystem.

Features:
- Ability to pre-configure multiple mail accounts
- Ability to delete mails in the mailbox after dumping them locally
- Support for HTML in the dump files


## Usage
```
{/path/to/config.json} {accountName} {emailSubjectContains} {/path/for/dump} {deleteAfterDump}
java -jar dmj.jar /home/ozzi/eclipse-workspace/dmj/config.json  External Reporting /home/ozzi/dump delete
```
## Command line output
```
Loading Mail Accounts
  |__ Outlook (outlook@test.local.ch)
  |__ External (trashmail@gmail.com)

Receiving Mails for 'External'
  |__ External has 18 mails in its inbox

Looking for Mails containing subject 'Reporting'
  |__  'Reporting 19-02-19' writing to /home/ozzi/dump/2019-02-19_10-25-07.mail
  |__  'Reporting 20-02-19' writing to /home/ozzi/dump/2019-02-20_10-25-22.mail
  |__  Flagged mail with subject 'Reporting 19-02-19' as deleted
  |__  Flagged mail with subject 'Reporting 20-02-19' as deleted
```
## Example of a dump
```
FROM: Outlook 2 <outlook2@test.local.ch>
TO: outlook@test.local.ch
SUBJECT: Reporting 20-02-19
SENT: Wed Feb 20 10:25:22 CET 2019
CONTENT:

1234 this is text

CONTENT HTML:

<html>
 <head></head>
 <body>
  <div style="font-family: arial, helvetica, sans-serif; font-size: 12pt; color: #000000">
   <div>
    <strong>1234 this is text</strong>
    <br>
   </div>
  </div>
 </body>
</html>
```
