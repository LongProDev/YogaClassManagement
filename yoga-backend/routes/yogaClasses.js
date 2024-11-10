const express = require('express');
const router = express.Router();
const YogaClass = require('../models/YogaClass');

// Get all yoga classes
router.get('/', async (req, res) => {
    try {
        const classes = await YogaClass.find();
        res.json(classes);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Create a new yoga class
router.post('/', async (req, res) => {
    const yogaClass = new YogaClass({
        dayOfWeek: req.body.dayOfWeek,
        time: req.body.time,
        capacity: req.body.capacity,
        duration: req.body.duration,
        price: req.body.price,
        type: req.body.type,
        description: req.body.description
    });

    try {
        const newClass = await yogaClass.save();
        res.status(201).json(newClass);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

// Update a yoga class
router.put('/:id', async (req, res) => {
    try {
        const yogaClass = await YogaClass.findById(req.params.id);
        if (!yogaClass) {
            return res.status(404).json({ message: 'Class not found' });
        }

        Object.assign(yogaClass, req.body);
        yogaClass.lastSynced = new Date();
        
        const updatedClass = await yogaClass.save();
        res.json(updatedClass);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

// Delete a yoga class
router.delete('/:id', async (req, res) => {
    try {
        const yogaClass = await YogaClass.findById(req.params.id);
        if (!yogaClass) {
            return res.status(404).json({ message: 'Class not found' });
        }

        await yogaClass.remove();
        res.json({ message: 'Class deleted' });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

module.exports = router;