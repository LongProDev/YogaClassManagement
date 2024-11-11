require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const yogaClassesRouter = require('./routes/yogaClasses');
const classInstancesRouter = require('./routes/classInstances');

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/api/yoga-classes', yogaClassesRouter);
app.use('/api/class-instances', classInstancesRouter);

// Database connection
mongoose.connect(process.env.MONGODB_URI)
    .then(() => console.log('Connected to Database'))
    .catch((error) => console.error('Database connection error:', error));

const db = mongoose.connection;
db.on('error', (error) => console.error(error));
db.once('open', () => console.log('Connected to Database'));

// Start server
const port = process.env.PORT || 3666;
app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});