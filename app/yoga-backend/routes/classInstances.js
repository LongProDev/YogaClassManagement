const express = require('express');
const router = express.Router();
const ClassInstance = require('../models/ClassInstance');

// Get all class instances
router.get('/', async (req, res) => {
    try {
        const instances = await ClassInstance.find();
        res.json(instances);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

// Create a new class instance
router.post('/', async (req, res) => {
    const instance = new ClassInstance({
        yogaClassId: req.body.yogaClassId,
        date: req.body.date,
        teacher: req.body.teacher,
        additionalComments: req.body.additionalComments
    });

    try {
        const newInstance = await instance.save();
        res.status(201).json(newInstance);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

// Update a class instance
router.put('/:id', async (req, res) => {
    try {
        const instance = await ClassInstance.findById(req.params.id);
        if (!instance) {
            return res.status(404).json({ message: 'Instance not found' });
        }

        Object.assign(instance, req.body);
        instance.lastSynced = new Date();
        
        const updatedInstance = await instance.save();
        res.json(updatedInstance);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

// Delete a class instance
router.delete('/:id', async (req, res) => {
    try {
        const instance = await ClassInstance.findById(req.params.id);
        if (!instance) {
            return res.status(404).json({ message: 'Instance not found' });
        }

        await instance.remove();
        res.json({ message: 'Instance deleted' });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});

module.exports = router;