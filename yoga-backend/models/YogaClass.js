const mongoose = require('mongoose');

const yogaClassSchema = new mongoose.Schema({
    dayOfWeek: {
        type: String,
        required: true
    },
    time: {
        type: String,
        required: true
    },
    capacity: {
        type: Number,
        required: true
    },
    duration: {
        type: Number,
        required: true
    },
    price: {
        type: Number,
        required: true
    },
    type: {
        type: String,
        required: true
    },
    description: {
        type: String
    },
    lastSynced: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('YogaClass', yogaClassSchema);