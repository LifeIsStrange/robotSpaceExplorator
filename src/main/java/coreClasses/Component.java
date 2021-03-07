package coreClasses;

import utils.Utils;

/**
 * a Mission robot/spacecraft is made of various Components
 */

// todo all components must report telemetry
// data sending is subject to increasing delays as the mission travels further away from Earth
// how to report progress if we sleep

// Network availability can be checked before a message is to be sent and if a network is
// available then it can be used to transmit the full message

public class Component {
    // reports payload size in kilobytes
    // Reports can be telemetry (100-10k bytes, frequent) or
    // data (100k-100MB, periodic)
    float payloadSize = Utils.getRandomNumberInRange(0.1F, 100000F);

    // report rate in hours
    float reportRate = Utils.getRandomNumberInRange(0.30F, 24F * 7F);
}