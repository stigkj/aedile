package com.sksamuel.aedile.core

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

class LoadingCacheTest : FunSpec() {
   init {

      test("LoadingCache should use support suspendable loading function") {
         val cache = caffeineBuilder<String, String>().build {
            delay(1)
            "bar"
         }
         cache.get("else") shouldBe "bar"
      }

      test("LoadingCache should support getOrPut") {

         val cache = caffeineBuilder<String, String>().build {
            delay(1)
            "bar"
         }

         cache.get("foo") {
            delay(1)
            "wibble"
         } shouldBe "wibble"

         cache.get("foo") {
            delay(1)
            "wobble"
         } shouldBe "wibble"
      }

      test("LoadingCache should handle exceptions in the compute function") {
         val cache = caffeineBuilder<String, String>().build() {
            delay(1)
            "bar"
         }
         shouldThrowAny {
            cache.get("foo") {
               error("kapow")
            }
         }
         cache.get("bar") {
            "baz"
         } shouldBe "baz"
      }

      test("LoadingCache should support suspendable put") {
         val cache = caffeineBuilder<String, String>().build {
            delay(1)
            "bar"
         }
         cache.put("foo") {
            delay(1)
            "wibble"
         }
         cache.get("foo") shouldBe "wibble"
         cache.get("else") shouldBe "bar"
      }

      test("LoadingCache should support getAll") {
         val cache = caffeineBuilder<String, String>().build {
            delay(1)
            "bar"
         }
         cache.put("foo") {
            delay(1)
            "wobble"
         }
         cache.put("bar") {
            delay(1)
            "wibble"
         }
         cache.put("baz") {
            delay(1)
            "wubble"
         }
         cache.getAll(listOf("foo", "bar")) shouldBe mapOf("foo" to "wobble", "bar" to "wibble")
      }

      test("LoadingCache should support asMap") {
         val cache = caffeineBuilder<String, String>().build {
            delay(1)
            "bar"
         }
         cache.put("foo") {
            delay(1)
            "wobble"
         }
         cache.put("bar") {
            delay(1)
            "wibble"
         }
         cache.asMap() shouldBe mapOf("foo" to "wobble", "bar" to "wibble")
      }

      test("LoadingCache.getIfPresent") {
         val cache = caffeineBuilder<String, String>().build {
            delay(1)
            "bar"
         }
         cache.getIfPresent("foo") shouldBe null
         cache.put("foo") { "baz" }
         cache.getIfPresent("foo") shouldBe "baz"
      }
   }
}
