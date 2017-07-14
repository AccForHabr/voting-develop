/********************************************************************************************
* SIDH: an efficient supersingular isogeny-based cryptography library for ephemeral 
*       Diffie-Hellman key exchange.
*
*    Copyright (c) Microsoft Corporation. All rights reserved.
*
*
* Abstract: API header file
*
*********************************************************************************************/  

#ifndef __SIDH_API_H__
#define __SIDH_API_H__


// For C++
#ifdef __cplusplus
extern "C" {
#endif


#include <SIDH.h>

/*********************** Key exchange API ***********************/ 

// Alice's key-pair generation
// It produces a private key pPrivateKeyA and computes the public key pPublicKeyA.
// The private key is an even integer in the range [2, oA-2], where oA = 2^372 (i.e., 372 bits in total).  
// The public key consists of 3 elements in GF(p751^2), i.e., 564 bytes.
// CurveIsogeny must be set up in advance using SIDH_curve_initialize().
CRYPTO_STATUS KeyGeneration_A(unsigned char* pPrivateKeyA, unsigned char* pPublicKeyA, PCurveIsogenyStruct CurveIsogeny);

// Bob's key-pair generation
// It produces a private key pPrivateKeyB and computes the public key pPublicKeyB.
// The private key is an integer in the range [1, oB-1], where oA = 3^239 (i.e., 379 bits in total).  
// The public key consists of 3 elements in GF(p751^2), i.e., 564 bytes.
// CurveIsogeny must be set up in advance using SIDH_curve_initialize().
CRYPTO_STATUS KeyGeneration_B(unsigned char* pPrivateKeyB, unsigned char* pPublicKeyB, PCurveIsogenyStruct CurveIsogeny);

// Alice's shared secret generation
// It produces a shared secret key pSharedSecretA using her secret key pPrivateKeyA and Bob's public key pPublicKeyB
// Inputs: Alice's pPrivateKeyA is an even integer in the range [2, oA-2], where oA = 2^372 (i.e., 372 bits in total). 
//         Bob's pPublicKeyB consists of 3 elements in GF(p751^2), i.e., 564 bytes.
//         "validate" flag that indicates if Alice must validate Bob's public key. 
// Output: a shared secret pSharedSecretA that consists of one element in GF(p751^2), i.e., 1502 bits in total. 
// CurveIsogeny must be set up in advance using SIDH_curve_initialize().
CRYPTO_STATUS SecretAgreement_A(unsigned char* pPrivateKeyA, unsigned char* pPublicKeyB, unsigned char* pSharedSecretA, bool validate, PCurveIsogenyStruct CurveIsogeny);

// Bob's shared secret generation
// It produces a shared secret key pSharedSecretB using his secret key pPrivateKeyB and Alice's public key pPublicKeyA
// Inputs: Bob's pPrivateKeyB is an integer in the range [1, oB-1], where oA = 3^239 (i.e., 379 bits in total). 
//         Alice's pPublicKeyA consists of 3 elements in GF(p751^2), i.e., 564 bytes.
//         "validate" flag that indicates if Bob must validate Alice's public key. 
// Output: a shared secret pSharedSecretB that consists of one element in GF(p751^2), i.e., 1502 bits in total. 
// CurveIsogeny must be set up in advance using SIDH_curve_initialize().
CRYPTO_STATUS SecretAgreement_B(unsigned char* pPrivateKeyB, unsigned char* pPublicKeyA, unsigned char* pSharedSecretB, bool validate, PCurveIsogenyStruct CurveIsogeny);

/*********************** Scalar multiplication API using BigMont ***********************/ 

// BigMont's scalar multiplication using the Montgomery ladder
// Inputs: x, the affine x-coordinate of a point P on BigMont: y^2=x^3+A*x^2+x, 
//         scalar m.
// Output: xout, the affine x-coordinate of m*(x:1)
// CurveIsogeny must be set up in advance using SIDH_curve_initialize().
CRYPTO_STATUS BigMont_ladder(unsigned char* x, digit_t* m, unsigned char* xout, PCurveIsogenyStruct CurveIsogeny);

/*********************** Scalar multiplication API using BigMont ***********************/ 

// BigMont's scalar multiplication using the Montgomery ladder
// Inputs: x, the affine x-coordinate of a point P on BigMont: y^2=x^3+A*x^2+x, 
//         scalar m.
// Output: xout, the affine x-coordinate of m*(x:1)
// CurveIsogeny must be set up in advance using SIDH_curve_initialize().
CRYPTO_STATUS BigMont_ladder(unsigned char* x, digit_t* m, unsigned char* xout, PCurveIsogenyStruct CurveIsogeny);


// Encoding of keys for isogeny system "SIDHp751" (wire format):
// ------------------------------------------------------------
// Elements over GF(p751) are encoded in 96 octets in little endian format (i.e., the least significant octet located at the leftmost position). 
// Elements (a+b*i) over GF(p751^2), where a and b are defined over GF(p751), are encoded as {b, a}, with b in the least significant position.
// Elements over Z_oA and Z_oB are encoded in 48 octets in little endian format. 
//
// Private keys pPrivateKeyA and pPrivateKeyB are defined in Z_oA and Z_oB (resp.) and can have values in the range [2, 2^372-2] and [1, 3^239-1], resp.
// In the key exchange API, they are encoded in 48 octets in little endian format. 
// Public keys pPublicKeyA and pPublicKeyB consist of four elements in GF(p751^2). In the key exchange API, they are encoded in 768 octets in little
// endian format. 
// Shared keys pSharedSecretA and pSharedSecretB consist of one element in GF(p751^2). In the key exchange API, they are encoded in 192 octets in little
// endian format. 


#ifdef __cplusplus
}
#endif


#endif
