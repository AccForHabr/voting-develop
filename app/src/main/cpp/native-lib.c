#include <jni.h>
#include <malloc.h>
#include <time.h>
#include <stdlib.h>
#include <SIDH.h>
#include <sha.h>
#include <android/log.h>
#include <SIDH_api.h>



/*****************************************************************************
* Choosing init Curve E0, generators (Pa,Qa), (Pb,Qb)
* Initialize curve isogeny structure pCurveIsogeny with static data extracted
* from pCurveIsogenyData.
******************************************************************************/
PCurveIsogenyStruct  Setup(PCurveIsogenyStaticData CurveIsogenyData)
{

    PCurveIsogenyStruct CurveIsogeny = { 0 };
    CRYPTO_STATUS Status = CRYPTO_SUCCESS;
    CurveIsogeny = SIDH_curve_allocate(CurveIsogenyData);
    if (CurveIsogeny == NULL)
        return 0;
    Status = SIDH_curve_initialize(CurveIsogeny,  CurveIsogenyData);
    if (Status != CRYPTO_SUCCESS)
        return 0;
    return CurveIsogeny;
}

/*****************************************************************************
* Key generation function
* Return pair of Public and Private keys
* Initialize curve isogeny structure pCurveIsogeny with static data
* extracted from pCurveIsogenyData.
******************************************************************************/
KeysStruct GenKey(PCurveIsogenyStaticData CurveIsogenyData, PCurveIsogenyStruct CurveIsogeny)
{
    KeysStruct Keys = { 0 };
    unsigned int i, pbytes = (CurveIsogenyData->pwordbits + 7) / 8;   // Number of bytes in a field element
    unsigned int obytes = (CurveIsogenyData->owordbits + 7) / 8;      // Number of bytes in an element in [1, order]
    CRYPTO_STATUS Status = CRYPTO_SUCCESS;
    bool ok = true;
    srand(time(NULL));
    // Allocating memory for private keys, public keys
    Keys.PrivateKey = (unsigned char*)calloc(1, obytes);        // One element in [1, order]
    Keys.PublicKey = (unsigned char*)calloc(1, 4 * 2 * pbytes);     // Four elements in GF(p^2)
    random_bytes_test(10, Keys.k);
    Status = KeyGeneration_A(Keys.PrivateKey, Keys.PublicKey, CurveIsogeny);
    if (Status != CRYPTO_SUCCESS)
        ok = false;

    return Keys;
}

/*****************************************************************************
* Compute j_invarint of Eab curve
* Parameter j contain computed value
* Return status value
******************************************************************************/
bool ComputejEncrypt(unsigned char * PublicKey, PCurveIsogenyStaticData CurveIsogenyData, PCurveIsogenyStruct CurveIsogeny,  unsigned char* j, unsigned char * imagesB)
{
    CRYPTO_STATUS Status = CRYPTO_SUCCESS;
    bool ok = true;
    unsigned char * IsogenyB;
    unsigned int obytes = (CurveIsogenyData->owordbits + 7) / 8;
    unsigned int i, pbytes = (CurveIsogenyData->pwordbits + 7) / 8;

    IsogenyB = (unsigned char*)calloc(1, obytes);

    Status = KeyGeneration_B(IsogenyB, imagesB, CurveIsogeny);

    if (Status != CRYPTO_SUCCESS)
        ok = false;
    Status = SecretAgreement_B(IsogenyB, PublicKey, j, false, CurveIsogeny);
    if (Status != CRYPTO_SUCCESS)
        ok = false;
    return ok;
}

/*****************************************************************************
* Compute j_invarint of Eba curve
* Parameter j contain computed value
* Return status value
******************************************************************************/
bool ComputejDecrypt(unsigned char * PrivateKey, PCurveIsogenyStaticData CurveIsogenyData, PCurveIsogenyStruct CurveIsogeny, unsigned char* imagesB, unsigned char* j)
{
    CRYPTO_STATUS Status = CRYPTO_SUCCESS;
    bool ok = true;

    Status = SecretAgreement_A(PrivateKey, imagesB, j, false, CurveIsogeny);
    if (Status != CRYPTO_SUCCESS)
        ok = false;
    return ok;
}

/*****************************************************************************
* Encryption function
* Parameter message contain ciphertext after computation
* Return curve Eb and images phiB(Pa),phiB(Qa)
******************************************************************************/
unsigned char* Encrypt(unsigned char * PublicKey, unsigned char k[10],  unsigned char message[] )
{
    int i = 0;
    bool ok = true;
    unsigned char* imagesB;
    unsigned char hash[SHA256_BLOCK_SIZE] = { 0 };
    PCurveIsogenyStaticData CurveIsogenyData = &CurveIsogeny_SIDHp751;
    PCurveIsogenyStruct	CurveIsogeny = Setup(&CurveIsogeny_SIDHp751);
    unsigned int pbytes = (CurveIsogenyData->pwordbits + 7) / 8;
    unsigned char* j = (unsigned char*)calloc(1, 2 * pbytes);
    imagesB = (unsigned char*)calloc(1, 4 * 2 * pbytes);
    ok = ComputejEncrypt(PublicKey, CurveIsogenyData, CurveIsogeny, j, imagesB);

    int j_len = strlen(j);
    j = (unsigned char*)realloc(j, (j_len+ 11) * sizeof(unsigned char));
    strncat(j, k, 10);

    shaCompute(j, hash);
    for (i = 0; i < strlen(message); i++)
        message[i] ^= hash[i];
    return imagesB;
}

/*****************************************************************************
* Decryption function
* Parameter cipher contain plaintext after computation
* Return status value
******************************************************************************/
bool Decrypt(unsigned char * PrivateKey, unsigned char k[10] , unsigned char cipher[] ,unsigned char imagesB[])
{
    int i = 0;
    bool ok = true;
    unsigned char hash[SHA256_BLOCK_SIZE] = { 0 };
    PCurveIsogenyStaticData CurveIsogenyData = &CurveIsogeny_SIDHp751;
    PCurveIsogenyStruct	CurveIsogeny = Setup(&CurveIsogeny_SIDHp751);
    unsigned int pbytes = (CurveIsogenyData->pwordbits + 7) / 8;
    unsigned char* j = (unsigned char*)calloc(1, 2 * pbytes);
    ok = ComputejDecrypt(PrivateKey, CurveIsogenyData, CurveIsogeny, imagesB,j);
    int j_len = strlen(j);
    j = (unsigned char*)realloc(j, (j_len + 11) * sizeof(unsigned char));

    strncat(j, k, 10);
    shaCompute(j, hash);

    for (i = 0; i < strlen(cipher); i++)
        cipher[i] ^= hash[i];

    return ok;
}

void shaCompute(unsigned char* message, unsigned char hash[SHA256_BLOCK_SIZE])
{
    SHA256_CTX ctx;
    sha256_init(&ctx);
    sha256_update(&ctx, message, strlen(message));
    sha256_final(&ctx, hash);
}

JNIEXPORT void JNICALL
Java_com_neoquest_voting_crypt_Encrypter_keyGen(JNIEnv *env, jobject obj, jcharArray k, jcharArray pub,jcharArray priv)
{
    PCurveIsogenyStruct CurveIsogeny = { 0 };
    KeysStruct key;
    jboolean isCopy = JNI_FALSE;
    CurveIsogeny = Setup(&CurveIsogeny_SIDHp751);
    key = GenKey(&CurveIsogeny_SIDHp751, CurveIsogeny);
    jchar *jarr = (*env)->GetCharArrayElements(env,k, &isCopy);
    jint size_k = (*env)->GetArrayLength(env,k);
    for (int i=0; i<size_k; i++)
        jarr[i] = key.k[i];
    (*env)->ReleaseCharArrayElements(env,k, jarr, JNI_COMMIT);

    jchar *jarr_pub = (*env)->GetCharArrayElements(env,pub, &isCopy);
    jint size_pub = (*env)->GetArrayLength(env,pub);
    for (int i=0; i<size_pub; i++)
        jarr_pub[i] = key.PublicKey[i];
    (*env)->ReleaseCharArrayElements(env,pub, jarr_pub, JNI_COMMIT);

    jchar *jarr_priv = (*env)->GetCharArrayElements(env, priv, &isCopy);
    jint size_priv = (*env)->GetArrayLength(env,priv);
    for (int i=0; i<size_priv; i++)
        jarr_priv[i] = key.PrivateKey[i];
    (*env)->ReleaseCharArrayElements(env,priv, jarr_priv, JNI_COMMIT);
}


JNIEXPORT void JNICALL
Java_com_neoquest_voting_crypt_Encrypter_encrypt(JNIEnv*  env,jobject obj,jcharArray publicKey,jcharArray k,jcharArray message, jcharArray images)
{

    unsigned char* imagesB;

    jchar *jarr  = (*env)->GetCharArrayElements(env, message, 0);
    jint size_m = (*env)->GetArrayLength(env,message);
    unsigned char * test_msg = (unsigned char*)malloc(size_m* sizeof(unsigned char));
    for(int i=0;i<size_m;i++)
        test_msg[i] = jarr[i];

    int* int_msg = (int)malloc(size_m* sizeof(int));

    jchar *jarr_pub  = (*env)->GetCharArrayElements(env,publicKey, 0);
    jint size_pub = (*env)->GetArrayLength(env,publicKey);
    unsigned char * test_pub = (unsigned char*)malloc(size_pub* sizeof(unsigned char));
    for(int i=0;i<size_pub;i++)
        test_pub[i] = jarr_pub[i];

    jchar *jarr_k  = (*env)->GetCharArrayElements(env,k, 0);
    jint size_k = (*env)->GetArrayLength(env,k);
    unsigned char * test_k = (unsigned char*)malloc(size_k* sizeof(unsigned char));
    for(int i=0;i<size_k;i++)
        test_k[i] = jarr_k[i];


    imagesB = Encrypt(test_pub, test_k, test_msg);

    for(int i=0;i<size_m;i++)
        jarr[i]=test_msg[i];


    (*env)->ReleaseCharArrayElements(env, message, jarr, JNI_COMMIT);
    jchar *jarr_images  = (*env)->GetCharArrayElements(env,images,0);
    jint size_images = (*env)->GetArrayLength(env,images);

    for(int i=0;i<size_images;i++)
        jarr_images[i]=imagesB[i];
    (*env)->ReleaseCharArrayElements(env, images, jarr_images, JNI_COMMIT);
    free(test_k);
    free(test_pub);
    free(test_msg);
}

JNIEXPORT void JNICALL
Java_com_neoquest_voting_crypt_Encrypter_decrypt(JNIEnv*  env,jobject obj,jcharArray privateKey,jcharArray k,jcharArray message,jcharArray images)
{

    jchar *jarr  = (*env)->GetCharArrayElements(env, message, 0);
    jint size_m = (*env)->GetArrayLength(env,message);
    unsigned char * test_msg = (unsigned char*)malloc(size_m* sizeof(unsigned char));
    for(int i=0;i<size_m;i++)
        test_msg[i] = jarr[i];

    jchar *jarr_priv  = (*env)->GetCharArrayElements(env,privateKey, 0);
    jint size_priv = (*env)->GetArrayLength(env,privateKey);
    unsigned char * test_priv = (unsigned char*)malloc(size_priv* sizeof(unsigned char));
    for(int i=0;i<size_priv;i++)
        test_priv[i] = jarr_priv[i];

    jchar *jarr_k  = (*env)->GetCharArrayElements(env,k, 0);
    jint size_k = (*env)->GetArrayLength(env,k);
    unsigned char * test_k = (unsigned char*)malloc(size_k* sizeof(unsigned char));
    for(int i=0;i<size_k;i++)
        test_k[i] = jarr_k[i];

    jchar *jarr_images  = (*env)->GetCharArrayElements(env,images,0);
    jint size_images = (*env)->GetArrayLength(env,images);
    unsigned char * test_images = (unsigned char*)malloc(size_images* sizeof(unsigned char));
    for(int i=0;i<size_images;i++)
        test_images[i] = jarr_images[i];

      Decrypt(test_priv, test_k, test_msg,  test_images);


    for(int i=0;i<size_m;i++)
        jarr[i]=test_msg[i];

    /* Release the body and commit changes */
    (*env)->ReleaseCharArrayElements(env, message, jarr, JNI_COMMIT);

}
