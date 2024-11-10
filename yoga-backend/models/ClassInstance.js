const mongoose = require('mongoose');

const classInstanceSchema = new mongoose.Schema({
    yogaClassId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'YogaClass',
        required: true
    },
    date: {
        type: String,
        required: true
    },
    teacher: {
        type: String,
        required: true
    },
    additionalComments: {
        type: String
    },
    lastSynced: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('ClassInstance', classInstanceSchema);