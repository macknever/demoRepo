package com.globalrelay.mdscache.performance.feeder

import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.globalrelay.mdscache.performance.helper.ConfigHelper.getConfig
import com.globalrelay.mdscache.performance.helper.Constant.*

import scala.jdk.CollectionConverters.*
import scala.util.Random

object MixedFeeder {

  val mdsSizes: List[Int] = getConfig().getAnyRefList(ConfigHelper.mdsHeaderSetSizes)
    .asScala
    .map(_.asInstanceOf[Int])
    .toList
  val mdsDistributions: List[Double] = getConfig().getAnyRefList(ConfigHelper.mdsHeaderSetDistributions)
    .asScala
    .map(_.asInstanceOf[Double])
    .toList

  val ucsSizes: List[Int] = getConfig().getAnyRefList(ConfigHelper.ucsHeaderSetSizes)
    .asScala
    .map(_.asInstanceOf[Int])
    .toList
  val ucsDistributions: List[Double] = getConfig().getAnyRefList(ConfigHelper.ucsHeaderSetDistributions)
    .asScala
    .map(_.asInstanceOf[Double])
    .toList

  val withMdsWildCardHeader: Boolean = getConfig().getBoolean(ConfigHelper.mdsWildcardHeaderPresent)
  val withUcsWildCardHeader: Boolean = getConfig().getBoolean(ConfigHelper.ucsWildcardHeaderPresent)
  val mdsWildCardRate: Float = getConfig().getDouble(ConfigHelper.mdsWildcardHeaderRate).toFloat
  val ucsWildCardRate: Float = getConfig().getDouble(ConfigHelper.ucsWildcardHeaderRate).toFloat

  val MIXED_MDS_DATA: Array[Set[Map[String, Any]]] = {
    val getCompanyNumberHeaderMap = transformFeeder(RandomCompanyNumberFeeder.DATA, GET_COMPANY_NUMBER_OPER)
    val getUserHeaderMap = transformFeeder(RandomUserFeeder.DATA
      .collect(id => Map[String, Any]("id" -> id)), GET_USER_OPER)
    val getUserProfileHeaderMap = transformFeeder(RandomUserProfileFeeder.DATA
      .collect(id => Map[String, Any]("smId" -> id)), GET_USER_PROFILE_OPER)
    val viewDisclaimerHeaderMap = transformFeeder(RandomDisclaimerFeeder.DATA
      .collect(id => Map[String, Any]("id" -> id)), VIEW_DISCLAIMER_OPER)
    val getUserFileCapabilitiesHeaderMap =
      transformFeeder(RandomUserCapabilitiesFeeder.DATA
        .collect(id => Map[String, Any]("id" -> id)), GET_USER_FILE_CAPABILITIES_OPER)

    val shuffledData = Random.shuffle((
      getCompanyNumberHeaderMap
        ++ getUserHeaderMap
        ++ getUserProfileHeaderMap
        ++ viewDisclaimerHeaderMap
        ++ getUserFileCapabilitiesHeaderMap
      ).toList)

    val distribution: Map[(Int, Int), Double] = buildDistributionMap(mdsSizes, mdsDistributions)

    val DATA = createSetsWithDistribution(shuffledData, distribution)

    if (withMdsWildCardHeader) {
      val mdsWildCardHeaderSize: Int = Math.round((DATA.length / (1 - mdsWildCardRate)) * mdsWildCardRate)

      val DATA_WITH_WILDCARD = DATA ++ generateWildcardHeader(mdsWildCardHeaderSize, MDS_OPERATION_LIST)
      DATA_WITH_WILDCARD
    } else {
      DATA
    }
  }

  val MIXED_UCS_DATA: Array[Set[Map[String, Any]]] = {
    val resolveContactHeaderMap = transformFeeder(RandomContactFeeder.DATA, RESOLVE_CONTACT_OPER)

    val shuffledData = Random.shuffle(resolveContactHeaderMap.toList)

    val distribution: Map[(Int, Int), Double] = buildDistributionMap(mdsSizes, mdsDistributions)

    val DATA = createSetsWithDistribution(shuffledData, distribution)

    if (withUcsWildCardHeader) {
      val ucsWildCardHeaderSize: Int = Math.round((DATA.length / (1 - mdsWildCardRate)) * mdsWildCardRate)
      val DATA_WITH_WILDCARD = DATA ++ generateWildcardHeader(ucsWildCardHeaderSize, UCS_OPERATION_LIST)
      DATA_WITH_WILDCARD
    } else {
      DATA
    }
  }

  /**
   * Transforms the map of request parameters into header map.
   *
   * @param feeder     an array of map which contains the parameters of a certain type of request.
   * @param feederType a string can identify the type of the request
   * @return a header map which can be used to consist kafka evict header
   */
  def transformFeeder(feeder: Array[Map[String, Any]], feederType: String): Array[Map[String, Any]] = {
    feeder.map { entry =>
      feederType match {
        case GET_COMPANY_NUMBER_OPER =>
          Map(
            API_VERSION_KEY -> V2,
            OPERATION_ID_KEY -> GET_COMPANY_NUMBER_OPER,
            ID_KEY -> entry(ID_KEY),
            NUMBER_KEY -> entry(NUMBER_KEY)
          )
        case GET_USER_OPER =>
          Map(
            API_VERSION_KEY -> V2,
            OPERATION_ID_KEY -> GET_USER_OPER,
            ID_KEY -> entry(ID_KEY)
          )
        case RESOLVE_CONTACT_OPER =>
          Map(
            API_VERSION_KEY -> V1,
            OPERATION_ID_KEY -> RESOLVE_CONTACT_OPER,
            ID_KEY -> entry(ID_KEY),
            ADDRESS_KEY -> entry(ADDRESS_KEY),
            ADDRESS_TYPE_KEY -> entry(ADDRESS_TYPE_KEY),
            CONTACT_ID_KEY -> entry(CONTACT_ID_KEY)
          )
        case GET_USER_PROFILE_OPER =>
          Map(
            API_VERSION_KEY -> V1,
            OPERATION_ID_KEY -> GET_USER_PROFILE_OPER,
            SMID_KEY -> entry(SMID_KEY),
          )
        case VIEW_DISCLAIMER_OPER =>
          Map(
            API_VERSION_KEY -> V2,
            OPERATION_ID_KEY -> VIEW_DISCLAIMER_OPER,
            ID_KEY -> entry(ID_KEY),
          )
        case GET_USER_FILE_CAPABILITIES_OPER =>
          Map(
            API_VERSION_KEY -> V2,
            OPERATION_ID_KEY -> GET_USER_FILE_CAPABILITIES_OPER,
            ID_KEY -> entry(ID_KEY)
          )
      }
    }
  }

  def generateWildcardHeader(size: Int, operationList: List[String]): Array[Set[Map[String, Any]]] = {
    Array.fill(size) {
      val randomKey = operationList(Random.nextInt(operationList.size))
      Set(generateWildcardHeader(randomKey))
    }
  }

  def generateWildcardHeader(headerType: String): Map[String, Any] = {
    headerType match {
      case GET_COMPANY_NUMBER_OPER =>
        Map(
          API_VERSION_KEY -> V2,
          OPERATION_ID_KEY -> GET_COMPANY_NUMBER_OPER
        )
      case GET_USER_OPER =>
        Map(
          API_VERSION_KEY -> V2,
          OPERATION_ID_KEY -> GET_USER_OPER
        )
      case RESOLVE_CONTACT_OPER =>
        Map(
          API_VERSION_KEY -> V1,
          OPERATION_ID_KEY -> RESOLVE_CONTACT_OPER
        )
      case GET_USER_PROFILE_OPER =>
        Map(
          API_VERSION_KEY -> V1,
          OPERATION_ID_KEY -> GET_USER_PROFILE_OPER,
        )
      case VIEW_DISCLAIMER_OPER =>
        Map(
          API_VERSION_KEY -> V2,
          OPERATION_ID_KEY -> VIEW_DISCLAIMER_OPER,
        )
      case GET_USER_FILE_CAPABILITIES_OPER =>
        Map(
          API_VERSION_KEY -> V2,
          OPERATION_ID_KEY -> GET_USER_FILE_CAPABILITIES_OPER,
        )
    }
  }

  /**
   * Groups the [[List]] of [[Map]] into an [[Array]] of [[Set]] of [[Map]].
   * The size of each [[Set]] follows given distribution
   *
   * @param data         List of Map needs to be grouped
   * @param distribution a map where keys are ranges `(start, end]` and values are probabilities
   *                     (the sum of all probabilities must equal 1).
   *                     e.g. Map((0, 5) -> 0.4, (6, 10) -> 0.6).
   * @return an Array of Sets.
   */
  def createSetsWithDistribution(
                                  data: List[Map[String, Any]],
                                  distribution: Map[(Int, Int), Double]
                                ): Array[Set[Map[String, Any]]] = {
    require(distribution.values.toArray.sum == 1, "Sum of distributions should equal to one")

    var remainingData = data
    val sets = collection.mutable.ArrayBuffer.empty[Set[Map[String, Any]]]

    while (remainingData.nonEmpty) {

      val chunkSize = generateRandomChunkSize(distribution)
      val actualChunkSize = Math.min(chunkSize, remainingData.size)

      val setData = remainingData.take(actualChunkSize).toSet
      sets.append(setData)

      remainingData = remainingData.drop(actualChunkSize)
    }

    sets.toArray
  }

  /**
   * Generates a random number based on the provided probability distributions.
   *
   * The input is a map where each key is a range `(start, end]`, and the value is the probability
   * of selecting a number from that range. This method first selects a range based on the probabilities,
   * then generates a random number uniformly from the selected range.
   *
   * Algorithm:
   * 1. Extract the ranges array (keys) and the probabilities array (values) from the input map.
   *    - e.g. ranges: [(0,5), (5,10)], probabilities: [0.4, 0.6]
   *      2. Compute cumulative probabilities from the probabilities array.
   *    - e.g.: [0.4, 0.6] -> [0.4, 1.0].
   *      3. Generate a random value between 0.0 and 1.0, following a uniform distribution.
   *      4. Determine which range the random value falls within:
   *    - e.g. 0.3 falls within [0, 0.4], so the corresponding range is (0, 5]. 0.8 falls within [0.4, 1.0],
   *      so the corresponding range is (6, 10].
   *      In such a way the random value will fall in the range in assigned probability.
   *      5. Generate a random integer uniformly from the selected range `(start, end]`.
   *
   * @param distributions a map where keys are ranges `(start, end]` and values are probabilities
   *                      (the sum of all probabilities must equal 1).
   *                      e.g. Map((0, 5) -> 0.4, (6, 10) -> 0.6).
   * @return a randomly generated number from one of the ranges, based on the probability distribution.
   *
   *         Example:
   * {{{
   * val distributions = Map((0, 5) -> 0.4, (6, 10) -> 0.6)
   * val result = generateRandomChunkSize(distributions)
   * // Possible result: 3 (from range 0-5) or 8 (from range 6-10)
   * }}}
   */
  def generateRandomChunkSize(distributions: Map[(Int, Int), Double]): Int = {

    val ranges = distributions.keys.toArray
    val probabilities = distributions.values.toArray

    require(probabilities.sum == 1, "Sum of distributions should equal to one")

    val cumulativeProbabilities = probabilities.scanLeft(0.0)(_ + _).tail
    val randomValue = Random.nextDouble()

    val selectedRangeIndex = cumulativeProbabilities.indexWhere(randomValue <= _)
    val (rangeMin, rangeMax) = ranges(selectedRangeIndex)

    Random.between(rangeMin, rangeMax + 1)
  }

  /**
   * Creates a distribution map where each range is associated with a probability.
   *
   * The key of the map is a range represented as a tuple `(start, end)`, and the value
   * is the probability that a number falls within that range. The ranges are defined
   * by consecutive sizes, and probabilities are assigned based on the `distributions` list.
   *
   * @param sizes         a list of integers defining the size of each range (i.e., the length of the range).
   *                      The ranges are consecutive, starting from 0, with each size determining the range's length.
   * @param distributions a list of probabilities (as doubles) corresponding to each range.
   *                      Each probability in this list is assigned to the respective range.
   * @return a map where each key is a range `(start, end)` and each value is the probability for that range.
   *
   *         Example:
   * {{{
   * val sizes = List(5, 10, 15)
   * val distributions = List(0.2, 0.5, 0.3)
   *
   * val result = buildDistributionMap(sizes, distributions)
   * // result: Map(
   * //   (0, 5)   -> 0.2,  // First range is from 0 to 5 with a probability of 0.2
   * //   (5, 10)  -> 0.5,  // Second range is from 5 to 10 with a probability of 0.5
   * //   (10, 15) -> 0.3   // Third range is from 10 to 15 with a probability of 0.3
   * // )
   * }}}
   */
  def buildDistributionMap(sizes: List[Int], distributions: List[Double]): Map[(Int, Int), Double] = {
    require(distributions.sum == 1, "Sum of distributions should equal to one")

    sizes.zip(distributions) // pairs sizes and distributions
      .foldLeft( // iterates through the list from left to right
        (Map.empty[(Int, Int), Double], 0) // init accumulator(empty map and the 1st start value) of foldLeft
      ) {
        case ((accumulator, start), (size, probability)) => // iterates the pairs of (size, probability)
          val end = start + size
          (accumulator + ((start, size) -> probability), end) // add (start, size) -> probability) into distribution map
      }._1 // return the first elements of the accumulator which is the map
  }

}
