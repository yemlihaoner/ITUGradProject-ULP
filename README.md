# ITUGradProject-ULP

### The graduation project: Undeniable Logging Protocol Implementation.

In this project, I aimed to implement a [secure logging protocol](https://doi.org/10.1049/iet-ifs.2014.0625) that is designed by Mehmet Tahir SANDIKKAYA. 

For the porpuse of implementation, I wrote the project in java programming language with socket programming technic. In order to secure logs [BouncyCastle](https://www.bouncycastle.org/java.html) libraries are used in encryption-decryption and sign-verify processes.


In the protocol, there are 3 main roles and one independent role; Bulletin Board, Users(Client), Providers(Host) and Verifiers. As these roles have direct connection with eachother, they also connects localhost:8080 (can be found .../main/index.html) to check logs and verify them if needed. Each log record has 4 different data in the log table which published by a Bulletin Board after encryption and signification processes. Anyone(ex. Verifiers) can retrieve data, read it and can secure system by verify signatures of logs. To understand protocol better, we need to understand the figure below.

![Scenario 1- Fig6.2](https://user-images.githubusercontent.com/32218322/121803363-d7322a00-cc49-11eb-9fb8-1452f76066de.png)

All the steps are carefully implemented for the main scenario. Details can be found below table from the SANDIKKAYA's paper. Beside this, the protocol also has an alternative end.

Main Scenario;

![Main Scenario](https://user-images.githubusercontent.com/32218322/121803426-2d06d200-cc4a-11eb-982a-c64148755a80.png)

Alternative end;

![Alternative Scenario](https://user-images.githubusercontent.com/32218322/121803457-558ecc00-cc4a-11eb-9630-846ce5b96c8a.png)



Designer and Owner of the protocol [Asst. Prof. Dr. Mehmet Tahir Sandıkkaya](https://github.com/sandikkaya)




