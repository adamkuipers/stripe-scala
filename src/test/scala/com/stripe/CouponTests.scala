package com.stripe

import org.expecty.Expecty
import utest._
import utest.ExecutionContext.RunNow

import fixtures._

object CouponTests extends TestSuite {
  val tests = TestSuite {
    val expect = new Expecty()

    "Coupons can be created" - {
      val coupon = Coupon.create(getUniqueCouponMap)

      expect(coupon.percentOff == 10)
    }

    "Coupons can be retrieved individually" - {
      val createdCoupon = Coupon.create(getUniqueCouponMap)
      val retrievedCoupon = Coupon.retrieve(createdCoupon.id)

      expect(createdCoupon == retrievedCoupon)
    }

    "Coupons can be deleted" - {
      val coupon = Coupon.create(getUniqueCouponMap)
      val deletedCoupon = coupon.delete()

      expect(deletedCoupon.deleted)
      expect(deletedCoupon.id == coupon.id)
    }

    "Coupons can be listed" - {
      val coupon = Coupon.create(getUniqueCouponMap)
      val coupons = Coupon.all().data

      expect(coupons.head.isInstanceOf[Coupon])
    }
  }
}
