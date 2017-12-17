package dballinger

import dballinger.Client._
import io.circe.Json
import org.scalatest.{FlatSpec, Matchers}
import io.circe.parser._
import cats.syntax.either._
import Generators._
import dballinger.Operations.Login
import dballinger.models.{Password, SessionId, Username}

class OperationsTest extends FlatSpec with Matchers {

  "Operations" should "login" in new JsonFixture {
    val username = aUsername
    val password = aPassword
    val sessionId = aSessionId
    val expectedPath = Path("auth/sessions")
    val response = loginResponse(sessionId)
    val expectedRequest = loginRequest(username, password)
    val post: Post = stubPost(expectedPath, expectedRequest, NoAuthentication, response)

    val loginResult = new Operations(null, post).login(username, password)

    loginResult should be(Right(sessionId))
  }

  it should "provide a node list" in new JsonFixture {
    val sessionId = aSessionId
    val login = sessionId.asRight
    val get: Get = stubGet(Path("nodes"), SessionAuthentication(sessionId), listNodesResponse())
    new Operations(get, null).listNodes(login) match {
      case Right(nodes) =>
        nodes.map(n => (n.id.value, n.name.value)) should be(
          List(
            ("d09101e4-c57b-4286-9f06-d7556445557e", "Porch light"),
            ("f785dd32-677a-4a6a-adb6-2175f493b812", "Tree")
          )
        )
      case _ => fail("Expected right")
    }
  }

  private def stubGet(expectedPath: Path, expectedAuth: Authentication, response: Json): Get = {
    (path, auth) => {
      if (path == expectedPath && auth == expectedAuth)
        response.asRight
      else
        fail(s"Unexpected request: path=$path, auth=$auth")
    }
  }

  private def stubPost(expectedPath: Path, expectedRequest: Json, expectedAuth: Authentication, response: Json): Post = {
    (path, json, auth) => {
      if (path == expectedPath && json == expectedRequest && auth == expectedAuth)
        response.asRight
      else
        fail(s"Unexpected request: path=$path, auth=$auth, json=$json")
    }
  }
}

trait JsonFixture {
  private def unsafeParse(str: String): Json = parse(str) match {
    case Right(json) => json
    case Left(f) => throw new Exception(f)
  }

  def loginRequest(username: Username, password: Password): Json = unsafeParse(
    s"""
       |{
       |    "sessions": [{
       |        "username": "${username.value}",
       |        "password": "${password.value}",
       |        "caller": "WEB"
       |    }]
       |}
       """.stripMargin
  )

  def loginResponse(sessionId: SessionId): Json = unsafeParse(
    s"""
       |{
       |    "meta": {},
       |    "links": {},
       |    "linked": {},
       |    "sessions": [{
       |        "id": "${sessionId.value}",
       |        "links": {},
       |        "username": "joe.bloggs@email.com",
       |        "userId": "cf82b9d8-8d0b-43b7-ae28-xxxxxxxxxxxx",
       |        "extCustomerLevel": 1,
       |        "latestSupportedApiVersion": "6",
       |        "sessionId": "${sessionId.value}"
       |    }]
       |}
       """.stripMargin
  )

  def listNodesResponse(): Json = unsafeParse(
    s"""
       |{
       |    "meta": {},
       |    "links": {},
       |    "linked": {},
       |    "nodes": [
       |        {
       |            "id": "d09101e4-c57b-4286-9f06-d7556445557e",
       |            "href": "https://api.prod.bgchprod.info:8443/omnia/nodes/d09101e4-c57b-4286-9f06-d7556445557e",
       |            "name": "Porch light",
       |            "nodeType": "http://alertme.com/schema/json/node.class.light.json#",
       |            "parentNodeId": "bb968c1e-73ae-43bc-80b8-b56cdb6de293",
       |            "lastSeen": 1513016701636,
       |            "createdOn": 1510953880801,
       |            "userId": "84a0c980-af86-48a3-ba09-de7f67216240",
       |            "ownerId": "84a0c980-af86-48a3-ba09-de7f67216240",
       |            "attributes": {
       |                "syntheticDeviceConfiguration": {
       |                    "targetValue": {
       |                        "schedule": [
       |                            {
       |                                "dayIndex": 1,
       |                                "transitions": [
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "06:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "08:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "16:00"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "21:30"
       |                                    }
       |                                ]
       |                            },
       |                            {
       |                                "dayIndex": 2,
       |                                "transitions": [
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "06:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "08:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "16:00"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "21:30"
       |                                    }
       |                                ]
       |                            },
       |                            {
       |                                "dayIndex": 3,
       |                                "transitions": [
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "06:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "08:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "16:00"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "21:30"
       |                                    }
       |                                ]
       |                            },
       |                            {
       |                                "dayIndex": 4,
       |                                "transitions": [
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "06:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "08:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "16:00"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "21:30"
       |                                    }
       |                                ]
       |                            },
       |                            {
       |                                "dayIndex": 5,
       |                                "transitions": [
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "06:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "08:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "16:00"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "21:30"
       |                                    }
       |                                ]
       |                            },
       |                            {
       |                                "dayIndex": 6,
       |                                "transitions": [
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "06:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "08:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "16:00"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "21:30"
       |                                    }
       |                                ]
       |                            },
       |                            {
       |                                "dayIndex": 7,
       |                                "transitions": [
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "06:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "08:30"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "brightness": 100,
       |                                            "state": "ON"
       |                                        },
       |                                        "time": "16:00"
       |                                    },
       |                                    {
       |                                        "action": {
       |                                            "state": "OFF"
       |                                        },
       |                                        "time": "21:30"
       |                                    }
       |                                ]
       |                            }
       |                        ],
       |                        "enabled": true
       |                    },
       |                    "targetSetTime": 1511628681041,
       |                    "targetExpiryTime": 1511628981041,
       |                    "targetSetTXId": "03180ea0-db98-423b-b195-581e8249010f",
       |                    "propertyStatus": "PENDING"
       |                },
       |                "nativeIdentifier": {
       |                    "reportedValue": "0FF8",
       |                    "displayValue": "0FF8",
       |                    "reportReceivedTime": 1513016701636,
       |                    "reportChangedTime": 1510953884424
       |                },
       |                "LQI": {
       |                    "reportedValue": 99,
       |                    "displayValue": 99,
       |                    "reportReceivedTime": 1513016701636,
       |                    "reportChangedTime": 1513003189129
       |                },
       |                "nodeType": {
       |                    "reportedValue": "http://alertme.com/schema/json/node.class.light.json#",
       |                    "displayValue": "http://alertme.com/schema/json/node.class.light.json#",
       |                    "reportReceivedTime": 1513016701636,
       |                    "reportChangedTime": 1510953884424
       |                },
       |                "powerSupply": {
       |                    "reportedValue": "AC",
       |                    "displayValue": "AC",
       |                    "reportReceivedTime": 1513016701636,
       |                    "reportChangedTime": 1510953884424
       |                },
       |                "manufacturer": {
       |                    "reportedValue": "Aurora",
       |                    "displayValue": "Aurora",
       |                    "reportReceivedTime": 1513016701636,
       |                    "reportChangedTime": 1510953884424
       |                },
       |                "RSSI": {
       |                    "reportedValue": -45,
       |                    "displayValue": -45,
       |                    "reportReceivedTime": 1513016701636,
       |                    "reportChangedTime": 1513013988862
       |                },
       |                "protocol": {
       |                    "reportedValue": "ZIGBEE",
       |                    "displayValue": "ZIGBEE",
       |                    "reportReceivedTime": 1512301475155,
       |                    "reportChangedTime": 1512301475155
       |                },
       |                "macAddress": {
       |                    "reportedValue": "00158D0001A71C44",
       |                    "displayValue": "00158D0001A71C44",
       |                    "reportReceivedTime": 1513016701636,
       |                    "reportChangedTime": 1510953884424
       |                },
       |                "brightness": {
       |                    "reportedValue": 100,
       |                    "targetValue": 100,
       |                    "displayValue": 100,
       |                    "reportReceivedTime": 1513016701636,
       |                    "reportChangedTime": 1513012502466,
       |                    "targetSetTime": 1512080134058,
       |                    "targetExpiryTime": 1512080434058,
       |                    "targetSetTXId": "e6b45bd0-2ab7-406c-a6e1-0483e2685e3c",
       |                    "propertyStatus": "COMPLETE"
       |                },
       |                "model": {
       |                    "reportedValue": "FWBulb01",
       |                    "displayValue": "FWBulb01",
       |                    "reportReceivedTime": 1513016701636,
       |                    "reportChangedTime": 1510953884424
       |                },
       |                "consumers": {
       |                    "targetValue": [
       |                        "d09101e4-c57b-4286-9f06-d7556445557e"
       |                    ],
       |                    "targetSetTime": 1511628681041,
       |                    "targetExpiryTime": 1511628981041,
       |                    "targetSetTXId": "03180ea0-db98-423b-b195-581e8249010f",
       |                    "propertyStatus": "PENDING"
       |                },
       |                "state": {
       |                    "reportedValue": "ON",
       |                    "targetValue": "OFF",
       |                    "displayValue": "ON",
       |                    "reportReceivedTime": 1513016701636,
       |                    "reportChangedTime": 1513009803263,
       |                    "targetSetTime": 1512080178721,
       |                    "targetExpiryTime": 1512080478721,
       |                    "targetSetTXId": "d39e6334-f81a-4e17-bcb1-a5e9f33207d9",
       |                    "propertyStatus": "COMPLETE"
       |                },
       |                "presence": {
       |                    "reportedValue": "PRESENT",
       |                    "displayValue": "PRESENT",
       |                    "reportReceivedTime": 1513016701636,
       |                    "reportChangedTime": 1512301488451
       |                },
       |                "softwareVersion": {
       |                    "reportedValue": "11250002",
       |                    "displayValue": "11250002",
       |                    "reportReceivedTime": 1513016701636,
       |                    "reportChangedTime": 1510953884424
       |                }
       |            }
       |        },
       |        {
       |            "id": "f785dd32-677a-4a6a-adb6-2175f493b812",
       |            "href": "https://api.prod.bgchprod.info:8443/omnia/nodes/f785dd32-677a-4a6a-adb6-2175f493b812",
       |            "name": "Tree",
       |            "nodeType": "http://alertme.com/schema/json/node.class.smartplug.json#",
       |            "parentNodeId": "bb968c1e-73ae-43bc-80b8-b56cdb6de293",
       |            "lastSeen": 1513016701548,
       |            "createdOn": 1512157000980,
       |            "userId": "84a0c980-af86-48a3-ba09-de7f67216240",
       |            "ownerId": "84a0c980-af86-48a3-ba09-de7f67216240",
       |            "attributes": {
       |                "powerConsumption": {
       |                    "reportedValue": 10,
       |                    "displayValue": 10,
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1513013199880
       |                },
       |                "nativeIdentifier": {
       |                    "reportedValue": "74CB",
       |                    "displayValue": "74CB",
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1512157001869
       |                },
       |                "LQI": {
       |                    "reportedValue": 100,
       |                    "displayValue": 100,
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1513006523523
       |                },
       |                "nodeType": {
       |                    "reportedValue": "http://alertme.com/schema/json/node.class.smartplug.json#",
       |                    "displayValue": "http://alertme.com/schema/json/node.class.smartplug.json#",
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1512157001869
       |                },
       |                "powerSupply": {
       |                    "reportedValue": "AC",
       |                    "displayValue": "AC",
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1512157001869
       |                },
       |                "manufacturer": {
       |                    "reportedValue": "Computime",
       |                    "displayValue": "Computime",
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1512157001869
       |                },
       |                "RSSI": {
       |                    "reportedValue": -54,
       |                    "displayValue": -54,
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1513016483311
       |                },
       |                "protocol": {
       |                    "reportedValue": "ZIGBEE",
       |                    "displayValue": "ZIGBEE",
       |                    "reportReceivedTime": 1512301474299,
       |                    "reportChangedTime": 1512301474299
       |                },
       |                "macAddress": {
       |                    "reportedValue": "001E5E09021D291E",
       |                    "displayValue": "001E5E09021D291E",
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1512157001869
       |                },
       |                "energyConsumed": {
       |                    "reportedValue": 618,
       |                    "displayValue": 618,
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1513016696898
       |                },
       |                "model": {
       |                    "reportedValue": "SLP2",
       |                    "displayValue": "SLP2",
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1512157001869
       |                },
       |                "state": {
       |                    "reportedValue": "ON",
       |                    "targetValue": "OFF",
       |                    "displayValue": "ON",
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1513009802214,
       |                    "targetSetTime": 1512371606494,
       |                    "targetExpiryTime": 1512371906494,
       |                    "targetSetTXId": "65f04b17-a846-42c7-a111-290c83305a63",
       |                    "propertyStatus": "COMPLETE"
       |                },
       |                "presence": {
       |                    "reportedValue": "PRESENT",
       |                    "displayValue": "PRESENT",
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1512326979093
       |                },
       |                "softwareVersion": {
       |                    "reportedValue": "02155120",
       |                    "displayValue": "02155120",
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1512157001869
       |                },
       |                "powerFactor": {
       |                    "reportedValue": 81,
       |                    "displayValue": 81,
       |                    "reportReceivedTime": 1513016701548,
       |                    "reportChangedTime": 1513016343173
       |                }
       |            }
       |        }
       |    ]
       |}
     """.stripMargin
  )
}