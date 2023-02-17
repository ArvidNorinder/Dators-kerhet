ECHO OFF


openssl genpkey -algorithm RSA -out CA.key
openssl req -new -key CA.key -out CA.csr
openssl x509 -req -days 365 -in CA.csr -signkey CA.key -out CA.crt


keytool -import -alias CA -file CA.crt -keystore clienttruststore -storepass password -noprompt

 
keytool -genkeypair -alias "Arvid Norinder ar7510no-s\Oscar Messelt os1617me-s\Rasmus Regnell ra3168re-s\Hjalmar Sandblom hj2850sa-s" -keyalg RSA -keystore clientkeystore -storepass password

 
keytool -certreq -alias "Arvid Norinder ar7510no-s\Oscar Messelt os1617me-s\Rasmus Regnell ra3168re-s\Hjalmar Sandblom hj2850sa-s" -keystore clientkeystore -file CA.csr -storepass password

 
openssl x509 -req -in CA.csr -CA CA.crt -CAkey CA.key -CAcreateserial -out user.crt -days 365



keytool -import -alias CA -file CA.crt -keystore clientkeystore -storepass password
keytool -import -alias "Arvid Norinder ar7510no-s\Oscar Messelt os1617me-s\Rasmus Regnell ra3168re-s\Hjalmar Sandblom hj2850sa-s" -file user.crt -keystore clientkeystore -storepass password


keytool -list -v -keystore clientkeystore -storepass password



-extfile v3.ext





keytool -genkeypair -alias myserver -keyalg RSA -keystore serverkeystore -storepass password
keytool -certreq -alias myserver -keystore serverkeystore -file myserver.csr -storepass password
openssl x509 -req -in myserver.csr -CA CA.crt -CAkey CA.key -CAcreateserial -out signedMyserver.crt -days 365
keytool -import -alias CA -file CA.crt -keystore serverkeystore -storepass password
keytool -import -alias myserver -file signedMyserver.crt -keystore serverkeystore -storepass password


keytool -import -alias CA -file CA.crt -keystore servertruststore -storepass password -noprompt





PAUSE